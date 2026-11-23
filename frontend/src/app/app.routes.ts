
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { StudentLayout } from './shared/layouts/student-layout/student-layout';

export const routes: Routes = [

  // Authentication
  {
    path: 'auth',
    loadComponent: () =>
      import('./shared/layouts/auth-layout/auth-layout')
        .then(m => m.AuthLayout),
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login')
            .then(m => m.Login)
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },

      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register')
            .then(m => m.Register)
      },
    ]
  },

  // Student Portal
  {
    path: 'student',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/layouts/student-layout/student-layout')
        .then(m => m.StudentLayout),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/student/dashboard/student-dashboard')
            .then(m => m.StudentDashboard)
      },
      {
        path: 'courses',
        loadComponent: () =>
          import('./features/student/courses/course-discovery')
            .then(m => m.CourseDiscovery)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'courses/:courseId',
        loadComponent: () =>
          import('./features/student/courses/course-details/course-details')
            .then(m => m.CourseDetails)
      },

      {
        path: 'learning/:courseId',
        loadComponent: () =>
          import('./features/student/learning/learning-workspace')
            .then(m => m.LearningWorkspace)
      },
      {
        path: 'learning',
        redirectTo: 'learning/courseId',
        pathMatch: 'full'
      },

      {
        path: 'quizzes/:quizId',
        loadComponent: () =>
          import('./features/student/quizzes/quiz-attempt/quiz-attempt')
            .then(m => m.QuizAttempt)
      },
      {
        path: 'quizzes',
        redirectTo: 'quizzes/1',
        pathMatch: 'full'
      },

      {
        path: 'bookmarks',
        loadComponent: () =>
          import('./features/student/bookmarks/bookmarks')
            .then(m => m.Bookmarks)
      },

      {
        path: 'notes',
        loadComponent: () =>
          import('./features/student/notes/notes')
            .then(m => m.Notes)
      },

      {
        path: 'profile',
        loadComponent: () =>
          import('./features/student/profile/profile')
            .then(m => m.Profile)
      },
    ]
  },

  // Admin Portal
  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/layouts/admin-layout/admin-layout')
        .then(m => m.AdminLayout),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/dashboard/admin-dashboard')
            .then(m => m.AdminDashboard)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // Default route
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'auth/login'
  }

];
