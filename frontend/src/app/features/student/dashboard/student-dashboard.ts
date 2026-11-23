import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { StudentDashboardService } from '../../../core/services/student-dashboard.service';
import { StudentDashboardResponse } from '../../../core/models/dashboard.models';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [RouterLink,DecimalPipe],
  templateUrl: './student-dashboard.html'
})
export class StudentDashboard implements OnInit {

  readonly authService = inject(AuthService);
  private readonly dashboardService = inject(StudentDashboardService);

  readonly dashboard = signal<StudentDashboardResponse | null>(null);
  readonly loading = signal(true);
  readonly errorMessage = signal('');

  readonly stats = computed(() => {
    const data = this.dashboard();

    return [
      {
        label: 'Enrolled Courses',
        value: data?.totalEnrolledCourses ?? 0,
        description: 'Courses in your library',
        icon: 'book',
        color: 'indigo'
      },
      {
        label: 'In Progress',
        value: data?.inProgressCourses ?? 0,
        description: 'Keep moving forward',
        icon: 'activity',
        color: 'amber'
      },
      {
        label: 'Completed Courses',
        value: data?.completedCourses ?? 0,
        description: 'Learning milestones achieved',
        icon: 'graduation',
        color: 'emerald'
      },
      {
        label: 'Lectures Completed',
        value: data?.totalLecturesCompleted ?? 0,
        description: 'Lessons successfully completed',
        icon: 'check',
        color: 'rose'
      }
    ];
  });

  readonly recentCourses = computed(
    () => this.dashboard()?.recentCourses ?? []
  );

  readonly continueLearning = computed(
    () => this.dashboard()?.continueLearning ?? null
  );

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.dashboardService.getDashboard().subscribe({
      next: (response) => {
        this.dashboard.set(response);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Dashboard API error:', error);

        this.errorMessage.set(
          error?.error?.message ??
          'We could not load your dashboard. Please try again.'
        );

        this.loading.set(false);
      }
    });
  }

  getProgressWidth(progress: number): number {
    return Math.min(100, Math.max(0, progress || 0));
  }

  formatStatus(status: string): string {
    return status
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, char => char.toUpperCase());
  }

  formatDuration(seconds: number | null | undefined): string {
    if (!seconds || seconds <= 0) return 'Start this lecture';

    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    return `${minutes}m ${remainingSeconds}s watched`;
  }
}