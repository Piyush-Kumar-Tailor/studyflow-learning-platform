import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { StudentDashboardResponse } from '../models/dashboard.models';

@Injectable({
  providedIn: 'root'
})
export class StudentDashboardService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/v1';

  getDashboard(): Observable<StudentDashboardResponse> {
    return this.http.get<StudentDashboardResponse>(
      `${this.apiUrl}/me/dashboard`
    );
  }
}