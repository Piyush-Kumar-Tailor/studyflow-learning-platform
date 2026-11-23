
import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { ActivatedRoute, RouterLink } from '@angular/router';

import { CourseCatalogService } from '../../../../core/services/course-catalog.service';
import { CurriculumService } from '../../../../core/services/curriculum.service';

import { CourseResponse } from '../../../../core/models/course.models';
import { LectureResponse } from '../../../../core/models/curriculum.models';
import { EnrollmentProgressService } from '../../../../core/services/enrollment-progress.service';
import { EnrollmentResponse } from '../../../../core/models/enrollment.models';

interface CourseDetail {
  id: number;
  slug: string;
  title: string;
  category: string;
  level: string;
  duration: string;
  lessons: number;
  rating: number;
  students: number;
  instructor: string;
  icon: string;
  description: string;
  outcomes: string[];
  language: string;
  thumbnailUrl: string | null;
}

interface Lecture {
  id: number;
  title: string;
  duration: string;
  type: 'video';
  completed: boolean;
}

interface CourseModule {
  id: number;
  title: string;
  description: string;
  lectures: Lecture[];
}

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './course-details.html'
})
export class CourseDetails implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly courseService = inject(CourseCatalogService);
  private readonly curriculumService = inject(CurriculumService);
  private readonly enrollmentService = inject(EnrollmentProgressService);

  readonly enrollment = signal<EnrollmentResponse | null>(null);
  readonly isEnrolling = signal(false);
  readonly enrollmentError = signal('');
  readonly enrollmentSuccess = signal('');

  readonly course = signal<CourseDetail | null>(null);
  readonly modules = signal<CourseModule[]>([]);

  readonly expandedModule = signal<number | null>(null);

  readonly loading = signal(true);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    const slug = this.route.snapshot.paramMap.get('courseId');

    if (!slug) {
      this.loading.set(false);
      this.errorMessage.set('Invalid course address.');
      return;
    }

    this.loadCourse(slug);
  }

  private loadCourse(slug: string): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.courseService.getCourseBySlug(slug).subscribe({

      next: (response) => {
        this.course.set(this.mapCourse(response));

        this.loadEnrollmentStatus(response.id);
        this.loadCurriculum(response.id);
      },

      error: (error) => {

        console.error('Course details loading failed:', error);

        this.errorMessage.set(
          error.error?.message ||
          'Unable to load course details.'
        );

        this.loading.set(false);
      }

    });
  }

  private loadEnrollmentStatus(courseId: number): void {
    this.enrollmentService.getMyEnrollments().subscribe({
      next: (enrollments) => {
        const existingEnrollment = enrollments.find(
          enrollment => enrollment.courseId === courseId
        );

        this.enrollment.set(existingEnrollment ?? null);
      },
      error: (error) => {
        console.error('Enrollment status loading failed:', error);
      }
    });
  }

  enrollInCourse(): void {
    const course = this.course();

    if (!course || this.isEnrolling() || this.enrollment()) {
      return;
    }

    this.isEnrolling.set(true);
    this.enrollmentError.set('');
    this.enrollmentSuccess.set('');

    this.enrollmentService.enrollInCourse(course.id).subscribe({
      next: (response) => {
        this.enrollment.set(response);
        this.enrollmentSuccess.set('You have successfully enrolled in this course!');
        this.isEnrolling.set(false);
      },
      error: (error) => {
        console.error('Course enrollment failed:', error);

        this.enrollmentError.set(
          error.error?.message || 'Unable to enroll in this course. Please try again.'
        );

        this.isEnrolling.set(false);
      }
    });
  }

  private loadCurriculum(courseId: number): void {

    this.curriculumService.getCourseCurriculum(courseId).subscribe({

      next: (response) => {

        const mappedModules: CourseModule[] =
          response.modules.map(module => ({
            id: module.id,
            title: module.title,
            description: module.description || '',
            lectures: module.lectures.map(
              lecture => this.mapLecture(lecture)
            )
          }));

        this.modules.set(mappedModules);

        this.expandedModule.set(
          mappedModules.length > 0 ? mappedModules[0].id : null
        );

        const totalLessons = mappedModules.reduce(
          (total, module) => total + module.lectures.length,
          0
        );

        this.course.update(course =>
          course ? { ...course, lessons: totalLessons } : null
        );

        this.loading.set(false);

        console.log('Course curriculum loaded:', response);
      },

      error: (error) => {

        console.error('Curriculum loading failed:', error);

        this.errorMessage.set(
          'Course details loaded, but the curriculum could not be loaded.'
        );

        this.loading.set(false);
      }

    });
  }

  private mapCourse(course: CourseResponse): CourseDetail {

    return {
      id: course.id,
      slug: course.slug,
      title: course.title,
      category: course.examName || 'General',
      level: this.normalizeDifficulty(course.difficulty),
      duration: 'Not specified',
      lessons: 0,
      rating: 0,
      students: 0,
      instructor: course.instructorName || 'StudyFlow Faculty',
      icon: this.getCourseIcon(course.title),
      description: course.description || 'Explore this course and start learning.',
      outcomes: [],
      language: course.language || 'Not specified',
      thumbnailUrl: course.thumbnailUrl
    };
  }

  private mapLecture(lecture: LectureResponse): Lecture {

    return {
      id: lecture.id,
      title: lecture.title,
      duration: this.formatDuration(lecture.durationSeconds),
      type: 'video',
      completed: false
    };
  }

  private normalizeDifficulty(value: string | null): string {

    if (!value) return 'Not specified';

    return value.charAt(0).toUpperCase() +
      value.slice(1).toLowerCase();
  }

  private getCourseIcon(title: string): string {

    const value = title.toLowerCase();

    if (value.includes('java')) return '☕';
    if (value.includes('python')) return '🐍';
    if (value.includes('database') || value.includes('sql')) return '🗄️';
    if (value.includes('network')) return '🌐';
    if (value.includes('algorithm')) return '🧠';
    if (value.includes('operating system')) return '💻';

    return '📚';
  }

  private formatDuration(seconds: number | null): string {

    if (seconds === null || seconds <= 0) {
      return 'Duration unavailable';
    }

    const minutes = Math.floor(seconds / 60);

    if (minutes < 60) {
      return `${minutes} min`;
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    return remainingMinutes > 0
      ? `${hours} hr ${remainingMinutes} min`
      : `${hours} hr`;
  }

  toggleModule(moduleId: number): void {

    this.expandedModule.update(current =>
      current === moduleId ? null : moduleId
    );
  }

  retryLoading(): void {

    const slug = this.route.snapshot.paramMap.get('courseId');

    if (slug) {
      this.loadCourse(slug);
    }
  }
}
