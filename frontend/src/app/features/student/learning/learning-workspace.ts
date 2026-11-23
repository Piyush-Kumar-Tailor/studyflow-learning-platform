
import {
  Component,
  OnInit,
  computed,
  inject,
  signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';

import { CurriculumService } from '../../../core/services/curriculum.service';
import { LectureResponse } from '../../../core/models/curriculum.models';
import { DomSanitizer } from '@angular/platform-browser';
import { EnrollmentProgressService } from '../../../core/services/enrollment-progress.service';
import {
  EnrollmentResponse,
  LectureProgressResponse,
  CourseProgressResponse
} from '../../../core/models/enrollment.models';
import {
  ElementRef,
  OnDestroy,
  ViewChild
} from '@angular/core';

interface YouTubePlayer {
  getCurrentTime(): number;
  seekTo(seconds: number, allowSeekAhead: boolean): void;
  loadVideoById(videoId: string, startSeconds?: number): void;
  destroy(): void;
}

interface YouTubePlayerEvent {
  target: YouTubePlayer;
  data: number;
}

interface YouTubePlayerApi {
  Player: new (
    element: HTMLIFrameElement,
    options: {
      events: {
        onReady: (event: YouTubePlayerEvent) => void;
        onStateChange: (event: YouTubePlayerEvent) => void;
      };
    }
  ) => YouTubePlayer;

  PlayerState: {
    PLAYING: number;
    PAUSED: number;
    ENDED: number;
  };
}

declare global {
  interface Window {
    YT?: YouTubePlayerApi;
    onYouTubeIframeAPIReady?: () => void;
  }
}


interface Lecture {
  id: number;
  title: string;
  duration: string;
  durationSeconds: number | null;
  lastPositionSeconds: number;
  type: 'video';
  completed: boolean;
  youtubeVideoId: string | null;
  description: string | null;
}

interface CourseModule {
  id: number;
  title: string;
  description: string | null;
  lectures: Lecture[];
}

@Component({
  selector: 'app-learning-workspace',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './learning-workspace.html',
  styleUrl: './learning-workspace.css'
})

export class LearningWorkspace implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly curriculumService = inject(CurriculumService);

  private readonly router = inject(Router);

  // All courses the current student has enrolled in.
  readonly enrolledCourses = signal<EnrollmentResponse[]>([]);

  // Special states for the empty workspace and invalid course selection.
  readonly hasNoEnrollments = signal(false);
  readonly courseNotEnrolled = signal(false);

  courseTitle = 'Loading course...';

  @ViewChild('youtubeFrame')
  private youtubeFrame?: ElementRef<HTMLIFrameElement>;

  private currentPlayerVideoId: string | null = null;

  private youtubePlayer: YouTubePlayer | null = null;

  private progressTimer: ReturnType<typeof setInterval> | null = null;

  private isSavingPosition = false;

  private youtubeApiPromise: Promise<void> | null = null;

  readonly courseProgress = signal<CourseProgressResponse | null>(null);

  readonly backendProgressPercentage = computed(
    () => this.courseProgress()?.progressPercentage ?? 0
  );

  readonly backendCompletedLectures = computed(
    () => this.courseProgress()?.completedLectures ?? 0
  );

  readonly backendTotalLectures = computed(
    () => this.courseProgress()?.totalLectures ?? 0
  );

  private readonly progressService = inject(EnrollmentProgressService);

  readonly enrollmentId = signal<number | null>(null);
  readonly enrollmentStatus = signal<string | null>(null);

  readonly isSavingProgress = signal(false);
  readonly progressError = signal('');
  readonly progressSuccess = signal('');

  readonly modules = signal<CourseModule[]>([]);

  readonly selectedLectureId = signal(0);
  readonly expandedModuleId = signal<number | null>(null);

  readonly notes = signal('');
  readonly savedNotes = signal<string[]>([]);
  readonly bookmarkedLectureIds = signal<number[]>([]);

  readonly loading = signal(true);
  readonly errorMessage = signal('');

  private courseId = 0;

  readonly allLectures = computed(() =>
    this.modules().flatMap(module => module.lectures)
  );

  readonly selectedLecture = computed(() =>
    this.allLectures().find(
      lecture => lecture.id === this.selectedLectureId()
    ) ?? null
  );

  readonly completedCount = computed(() =>
    this.allLectures().filter(lecture => lecture.completed).length
  );

  readonly totalLectures = computed(() =>
    this.allLectures().length
  );

  readonly progress = computed(() => {
    const total = this.totalLectures();

    return total === 0
      ? 0
      : Math.round((this.completedCount() / total) * 100);
  });

  readonly isBookmarked = computed(() =>
    this.bookmarkedLectureIds().includes(this.selectedLectureId())
  );

  private readonly sanitizer = inject(DomSanitizer);
  readonly videoEmbedUrl = computed(() => {
    const videoId = this.selectedLecture()?.youtubeVideoId;

    if (!videoId) {
      return null;
    }

    return this.sanitizer.bypassSecurityTrustResourceUrl(
      `https://www.youtube-nocookie.com/embed/${encodeURIComponent(videoId)}?rel=0&enablejsapi=1&playsinline=1`
    );
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const courseId = Number(params.get('courseId'));

      if (!courseId || Number.isNaN(courseId)) {
        this.modules.set([]);
        this.errorMessage.set('Invalid course ID.');
        this.loading.set(false);
        return;
      }

      this.courseId = courseId;

      // Reset previous course data.
      this.modules.set([]);
      this.courseProgress.set(null);
      this.enrollmentId.set(null);
      this.enrollmentStatus.set(null);
      this.selectedLectureId.set(0);
      this.expandedModuleId.set(null);
      this.errorMessage.set('');
      this.progressError.set('');
      this.progressSuccess.set('');
      this.loading.set(true);

      this.stopProgressTracking();

      // Verify enrollment before loading curriculum.
      this.loadEnrollmentAndProgress();

      this.enrolledCourses.set([]);
      this.hasNoEnrollments.set(false);
      this.courseNotEnrolled.set(false);
    });

    this.route.queryParamMap.subscribe(params => {
      const lectureId = Number(params.get('lectureId'));

      if (lectureId > 0) {
        this.selectLectureFromRoute(lectureId);
      }
    });
  }

  getLessonNumber(lectureId: number): number {
    return this.allLectures().findIndex(
      lecture => lecture.id === lectureId
    ) + 1;
  }

  getCompletedInModule(module: CourseModule): number {
    return module.lectures.filter(lecture => lecture.completed).length;
  }

  getSelectedLessonNumber(): number {
    return this.getLessonNumber(this.selectedLectureId());
  }

  initializeYoutubePlayer(): void {
    const lecture = this.selectedLecture();
    const iframe = this.youtubeFrame?.nativeElement;

    if (!iframe || !lecture?.youtubeVideoId) {
      return;
    }

    this.loadYoutubeApi()
      .then(() => {
        if (!window.YT || iframe !== this.youtubeFrame?.nativeElement) {
          return;
        }

        if (this.youtubePlayer) {
          if (this.currentPlayerVideoId !== lecture.youtubeVideoId) {
            this.stopProgressTracking();
            this.savePlaybackPosition();

            this.currentPlayerVideoId = lecture.youtubeVideoId;

            const videoId = lecture.youtubeVideoId;

            if (this.currentPlayerVideoId !== videoId && videoId) {
              this.stopProgressTracking();

              this.currentPlayerVideoId = videoId;

              this.youtubePlayer.loadVideoById(
                videoId,
                lecture.lastPositionSeconds
              );
            }
          }

          return;
        }

        this.currentPlayerVideoId = lecture.youtubeVideoId;

        this.youtubePlayer = new window.YT.Player(iframe, {
          events: {
            onReady: (event) => {
              if (lecture.lastPositionSeconds > 0) {
                event.target.seekTo(lecture.lastPositionSeconds, true);
              }
            },
            onStateChange: (event) => {
              this.handleYoutubeStateChange(event);
            }
          }
        });
      })
      .catch(error => {
        console.error('YouTube player initialization failed:', error);
      });
  }

  switchCourse(courseId: number): void {
    const enrollment = this.enrolledCourses().find(
      item => Number(item.courseId) === Number(courseId)
    );

    if (!enrollment || courseId === this.courseId) {
      return;
    }

    // Save the currently selected lecture before switching courses.
    this.prepareLectureChange();

    this.router.navigate(['/student/learning', courseId]);
  }

  loadCurriculum(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.progressError.set('');

    this.curriculumService.getCourseCurriculum(this.courseId).subscribe({
      next: (data) => {
        this.courseTitle = data.courseTitle;

        const mappedModules: CourseModule[] = data.modules.map(module => ({
          id: module.id,
          title: module.title,
          description: module.description,
          lectures: module.lectures.map(lecture => this.mapLecture(lecture))
        }));

        this.modules.set(mappedModules);

        // Load progress only after the correct course curriculum is available.
        const enrollmentId = this.enrollmentId();

        if (enrollmentId) {
          this.loadSavedLectureProgress(enrollmentId);
          this.loadCourseProgress(enrollmentId);
        }

        const firstLecture = this.allLectures()[0];

        if (firstLecture) {
          this.selectedLectureId.set(firstLecture.id);
          this.expandedModuleId.set(
            mappedModules.find(module =>
              module.lectures.some(lecture => lecture.id === firstLecture.id)
            )?.id ?? null
          );
        }

        this.loading.set(false);

      },
      error: (error) => {
        console.error('Curriculum loading failed:', error);
        this.errorMessage.set(
          'Unable to load course curriculum. Please try again.'
        );
        this.loading.set(false);
      }
    });
  }

  private loadYoutubeApi(): Promise<void> {
    if (window.YT) {
      return Promise.resolve();
    }

    if (this.youtubeApiPromise) {
      return this.youtubeApiPromise;
    }

    this.youtubeApiPromise = new Promise<void>((resolve, reject) => {
      window.onYouTubeIframeAPIReady = () => resolve();

      const existingScript = document.querySelector(
        'script[src="https://www.youtube.com/iframe_api"]'
      );

      if (!existingScript) {
        const script = document.createElement('script');

        script.src = 'https://www.youtube.com/iframe_api';
        script.async = true;

        script.onerror = () => {
          this.youtubeApiPromise = null;
          reject(new Error('Unable to load YouTube Player API.'));
        };

        document.head.appendChild(script);
      }
    });

    return this.youtubeApiPromise;
  }

  private loadEnrollmentAndProgress(): void {
    this.progressService.getMyEnrollments().subscribe({
      next: (enrollments: EnrollmentResponse[]) => {
        // Store all enrollments for the course selector.
        this.enrolledCourses.set(enrollments);

        this.hasNoEnrollments.set(enrollments.length === 0);
        this.courseNotEnrolled.set(false);

        if (enrollments.length === 0) {
          this.enrollmentId.set(null);
          this.enrollmentStatus.set(null);
          this.courseProgress.set(null);
          this.modules.set([]);
          this.courseTitle = 'Your learning journey starts here';
          this.loading.set(false);
          return;
        }

        const enrollment = enrollments.find(
          item => Number(item.courseId) === Number(this.courseId)
        );

        if (!enrollment) {
          this.enrollmentId.set(null);
          this.enrollmentStatus.set(null);
          this.courseProgress.set(null);
          this.modules.set([]);
          this.courseNotEnrolled.set(true);
          this.courseTitle = 'Choose an enrolled course';
          this.loading.set(false);
          return;
        }

        this.enrollmentId.set(enrollment.id);
        this.enrollmentStatus.set(enrollment.status);

        console.log('Selected enrollment:', enrollment);

        this.loadCurriculum();
      },

      error: (error) => {
        console.error('Failed to load enrollments:', error);

        this.modules.set([]);
        this.errorMessage.set(
          'Unable to verify your enrollment. Please try again.'
        );

        this.loading.set(false);
      }
    });
  }

  private handleYoutubeStateChange(event: YouTubePlayerEvent): void {
    const states = window.YT?.PlayerState;

    if (!states) {
      return;
    }

    if (event.data === states.PLAYING) {
      this.startProgressTracking();
    }

    if (
      event.data === states.PAUSED ||
      event.data === states.ENDED
    ) {
      this.stopProgressTracking();
      this.savePlaybackPosition();
    }
  }

  private startProgressTracking(): void {
    this.stopProgressTracking();

    this.progressTimer = setInterval(() => {
      this.savePlaybackPosition();
    }, 15000);
  }

  private stopProgressTracking(): void {
    if (this.progressTimer !== null) {
      clearInterval(this.progressTimer);
      this.progressTimer = null;
    }
  }

  private savePlaybackPosition(): void {
    const lecture = this.selectedLecture();
    const enrollmentId = this.enrollmentId();
    const player = this.youtubePlayer;

    if (
      !lecture ||
      !player ||
      !enrollmentId ||
      lecture.completed ||
      this.enrollmentStatus() !== 'IN_PROGRESS' ||
      this.isSavingPosition
    ) {
      return;
    }

    const position = Math.floor(player.getCurrentTime());

    if (!Number.isFinite(position) || position <= 0) {
      return;
    }

    this.isSavingPosition = true;

    this.progressService.updateLectureProgress(
      enrollmentId,
      lecture.id,
      {
        lastPositionSeconds: position,
        completed: false
      }
    ).subscribe({
      next: (response) => {
        this.modules.update(modules =>
          modules.map(module => ({
            ...module,
            lectures: module.lectures.map(item =>
              item.id === lecture.id
                ? {
                  ...item,
                  lastPositionSeconds: response.lastPositionSeconds
                }
                : item
            )
          }))
        );

        this.isSavingPosition = false;
      },
      error: (error) => {
        console.error('Watch position could not be saved:', error);
        this.isSavingPosition = false;
      }
    });
  }

  private prepareLectureChange(): void {
    this.savePlaybackPosition();
    this.stopProgressTracking();
  }

  private loadSavedLectureProgress(enrollmentId: number): void {
    this.progressService.getLectureProgress(enrollmentId).subscribe({
      next: (savedProgress: LectureProgressResponse[]) => {
        const progressMap = new Map(
          savedProgress.map(item => [item.lectureId, item])
        );

        this.modules.update(modules =>
          modules.map(module => ({
            ...module,
            lectures: module.lectures.map(lecture => {
              const saved = progressMap.get(lecture.id);

              return {
                ...lecture,
                completed: saved?.status === 'COMPLETED',
                lastPositionSeconds: saved?.lastPositionSeconds ?? 0
              };
            })
          }))
        );
      },
      error: (error) => {
        console.error('Lecture progress loading failed:', error);
        this.progressError.set(
          'Unable to load saved lecture progress.'
        );
      }
    });
  }

  markAsCompleted(): void {
    const lecture = this.selectedLecture();
    const enrollment = this.enrollmentId();

    if (!lecture || !enrollment) {
      this.progressError.set(
        'You must be enrolled in this course to save lecture progress.'
      );
      return;
    }

    if (lecture.completed) {
      return;
    }

    if (this.enrollmentStatus() !== 'IN_PROGRESS') {
      this.progressError.set(
        'Your enrollment is not active for progress updates.'
      );
      return;
    }

    this.isSavingProgress.set(true);
    this.progressError.set('');
    this.progressSuccess.set('');

    this.progressService.updateLectureProgress(
      enrollment,
      lecture.id,
      {
        lastPositionSeconds: lecture.lastPositionSeconds ?? 0,
        completed: true
      }
    ).subscribe({
      next: (response) => {
        this.modules.update(modules =>
          modules.map(module => ({
            ...module,
            lectures: module.lectures.map(item =>
              item.id === lecture.id
                ? {
                  ...item,
                  completed: response.status === 'COMPLETED'
                }
                : item
            )
          }))
        );

        this.progressSuccess.set(
          'Lecture completed and progress saved!'
        );

        this.isSavingProgress.set(false);

        this.loadCourseProgress(enrollment);
      },

      error: (error) => {
        console.error('Progress update failed:', error);

        this.progressError.set(
          error.error?.message ||
          'Unable to save progress. Please try again.'
        );

        this.isSavingProgress.set(false);
      }
    });
  }

  private loadCourseProgress(enrollmentId: number): void {
    this.progressService.getCourseProgress(enrollmentId).subscribe({
      next: (response) => {
        this.courseProgress.set(response);
        this.enrollmentStatus.set(response.enrollmentStatus);
      },
      error: (error) => {
        console.error('Course progress loading failed:', error);
        this.progressError.set(
          'Unable to load overall course progress.'
        );
      }
    });
  }

  ngOnDestroy(): void {
    this.savePlaybackPosition();
    this.stopProgressTracking();

    this.youtubePlayer?.destroy();
    this.youtubePlayer = null;
  }



  private mapLecture(lecture: LectureResponse): Lecture {
    return {
      id: lecture.id,
      title: lecture.title,
      duration: this.formatDuration(lecture.durationSeconds),
      durationSeconds: lecture.durationSeconds,
      lastPositionSeconds: 0,
      type: 'video',
      completed: false,
      youtubeVideoId: lecture.youtubeVideoId,
      description: lecture.description
    };
  }

  private formatDuration(seconds: number | null): string {
    if (seconds === null || seconds <= 0) {
      return 'Duration unavailable';
    }

    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    if (remainingSeconds === 0) {
      return `${minutes} min`;
    }

    return `${minutes} min ${remainingSeconds} sec`;
  }

  private selectLectureFromRoute(lectureId: number): void {
    this.prepareLectureChange();
    const module = this.modules().find(item =>
      item.lectures.some(lecture => lecture.id === lectureId)
    );

    if (!module) {
      return;
    }

    this.selectedLectureId.set(lectureId);
    this.expandedModuleId.set(module.id);
    this.notes.set('');
  }

  selectLecture(lectureId: number, moduleId: number): void {
    this.prepareLectureChange();
    this.selectedLectureId.set(lectureId);
    this.expandedModuleId.set(moduleId);
    this.notes.set('');
  }

  toggleModule(moduleId: number): void {
    this.expandedModuleId.update(current =>
      current === moduleId ? null : moduleId
    );
  }



  toggleBookmark(): void {
    const id = this.selectedLectureId();

    this.bookmarkedLectureIds.update(ids =>
      ids.includes(id)
        ? ids.filter(item => item !== id)
        : [...ids, id]
    );
  }

  saveNote(): void {
    const note = this.notes().trim();

    if (!note) {
      return;
    }

    this.savedNotes.update(notes => [...notes, note]);
    this.notes.set('');
  }

  selectNextLecture(): void {
    const lectures = this.allLectures();

    const currentIndex = lectures.findIndex(
      lecture => lecture.id === this.selectedLectureId()
    );

    if (currentIndex < lectures.length - 1) {
      const nextLecture = lectures[currentIndex + 1];

      const module = this.modules().find(item =>
        item.lectures.some(lecture => lecture.id === nextLecture.id)
      );

      if (module) {
        this.selectLecture(nextLecture.id, module.id);
      }
    }
  }

  selectPreviousLecture(): void {
    const lectures = this.allLectures();

    const currentIndex = lectures.findIndex(
      lecture => lecture.id === this.selectedLectureId()
    );

    if (currentIndex > 0) {
      const previousLecture = lectures[currentIndex - 1];

      const module = this.modules().find(item =>
        item.lectures.some(lecture => lecture.id === previousLecture.id)
      );

      if (module) {
        this.selectLecture(previousLecture.id, module.id);
      }
    }
  }
}
