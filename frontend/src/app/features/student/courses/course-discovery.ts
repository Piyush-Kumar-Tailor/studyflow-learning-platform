
import {
  Component,
  OnInit,
  computed,
  inject,
  signal
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { CourseCatalogService } from '../../../core/services/course-catalog.service';
import { CourseResponse } from '../../../core/models/course.models';

interface Course {
  id: number;
  title: string;
  description: string;
  exam: string;
  difficulty: 'Beginner' | 'Intermediate' | 'Advanced';
  instructor: string;
  duration: string;
  lessons: number;
  rating: number;
  students: number;
  icon: string;
  color: string;
  slug: string;
  thumbnailUrl: string | null;
}

@Component({
  selector: 'app-course-discovery',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './course-discovery.html'
})
export class CourseDiscovery implements OnInit {

  private readonly courseService = inject(CourseCatalogService);

  readonly searchQuery = signal('');
  readonly selectedExam = signal('All Exams');
  readonly selectedDifficulty = signal('All Levels');

  readonly courses = signal<Course[]>([]);

  readonly loading = signal(false);
  readonly errorMessage = signal('');

  readonly currentPage = signal(0);
  readonly pageSize = signal(12);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly examCategories = [
    'All Exams',
    'Programming',
    'University',
    'Competitive Exams'
  ];

  readonly difficultyLevels = [
    'All Levels',
    'Beginner',
    'Intermediate',
    'Advanced'
  ];

  readonly filteredCourses = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const exam = this.selectedExam();
    const difficulty = this.selectedDifficulty();

    return this.courses().filter(course => {

      const matchesSearch =
        !query ||
        course.title.toLowerCase().includes(query) ||
        course.description.toLowerCase().includes(query);

      const matchesExam =
        exam === 'All Exams' || course.exam === exam;

      const matchesDifficulty =
        difficulty === 'All Levels' ||
        course.difficulty === difficulty;

      return matchesSearch && matchesExam && matchesDifficulty;
    });
  });

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(page: number = 0): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.courseService.getCourses(page, this.pageSize()).subscribe({

      next: (response) => {

        const mappedCourses = response.content.map(course =>
          this.mapCourse(course)
        );

        this.courses.set(mappedCourses);

        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);

        this.loading.set(false);

        console.log('Courses loaded:', response);
      },

      error: (error) => {

        console.error('Course loading failed:', error);

        this.errorMessage.set(
          error.error?.message ||
          'Unable to load courses. Please try again.'
        );

        this.loading.set(false);
      }

    });
  }

  private mapCourse(course: CourseResponse): Course {

    const difficulty = this.normalizeDifficulty(course.difficulty);

    return {
      id: course.id,
      title: course.title,
      description: course.description || 'Explore this course and start learning.',
      exam: course.examName || 'General',
      difficulty,
      instructor: course.instructorName || 'StudyFlow Faculty',

      // These details are not currently included in CourseResponse.
      duration: 'Not specified',
      lessons: 0,
      rating: 0,
      students: 0,

      icon: this.getCourseIcon(course.title),
      color: this.getCourseColor(course.title),

      slug: course.slug,
      thumbnailUrl: course.thumbnailUrl
    };
  }

  private normalizeDifficulty(
    difficulty: string | null
  ): 'Beginner' | 'Intermediate' | 'Advanced' {

    const value = difficulty?.toUpperCase();

    if (value === 'ADVANCED') return 'Advanced';
    if (value === 'INTERMEDIATE') return 'Intermediate';

    return 'Beginner';
  }

  private getCourseIcon(title: string): string {

    const value = title.toLowerCase();

    if (value.includes('java')) return '☕';
    if (value.includes('python')) return '🐍';
    if (value.includes('database') || value.includes('sql')) return '🗄️';
    if (value.includes('network')) return '🌐';
    if (value.includes('algorithm') || value.includes('data structure')) return '🧠';
    if (value.includes('operating system')) return '💻';
    if (value.includes('web')) return '🌍';

    return '📚';
  }

  private getCourseColor(title: string): string {

    const value = title.toLowerCase();

    if (value.includes('java')) return 'bg-orange-500/10';
    if (value.includes('python')) return 'bg-yellow-500/10';
    if (value.includes('database')) return 'bg-teal-500/10';
    if (value.includes('network')) return 'bg-cyan-500/10';
    if (value.includes('algorithm')) return 'bg-indigo-500/10';

    return 'bg-primary/10';
  }

  nextPage(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.loadCourses(this.currentPage() + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.loadCourses(this.currentPage() - 1);
    }
  }

  resetFilters(): void {
    this.searchQuery.set('');
    this.selectedExam.set('All Exams');
    this.selectedDifficulty.set('All Levels');
  }

  retryLoading(): void {
    this.loadCourses(this.currentPage());
  }
}
