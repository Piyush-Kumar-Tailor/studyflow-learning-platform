
import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  template: `
    <section class="space-y-3">
      <p class="text-sm font-medium text-primary">ADMINISTRATION</p>
      <h1 class="text-3xl font-bold">Admin Dashboard</h1>
      <p class="text-muted">
        Manage students, courses, exams and platform activities.
      </p>
    </section>
  `
})
export class AdminDashboard {}
