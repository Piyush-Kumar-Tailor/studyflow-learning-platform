import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  EnrollmentResponse,
  LectureProgressResponse,
  CourseProgressResponse,
  UpdateLectureProgressRequest
} from '../models/enrollment.models';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentProgressService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/v1';

  // Get all enrollments of the logged-in student
  getMyEnrollments(): Observable<EnrollmentResponse[]> {
    return this.http.get<EnrollmentResponse[]>(
      `${this.apiUrl}/me/enrollments`
    );
  }

  // Get progress of all lectures in an enrollment
  getLectureProgress(
    enrollmentId: number
  ): Observable<LectureProgressResponse[]> {
    return this.http.get<LectureProgressResponse[]>(
      `${this.apiUrl}/me/enrollments/${enrollmentId}/lecture-progress`
    );
  }

  // Get overall course progress
  getCourseProgress(
    enrollmentId: number
  ): Observable<CourseProgressResponse> {
    return this.http.get<CourseProgressResponse>(
      `${this.apiUrl}/me/enrollments/${enrollmentId}/progress`
    );
  }

  // Update lecture progress
  updateLectureProgress(
    enrollmentId: number,
    lectureId: number,
    request: UpdateLectureProgressRequest
  ): Observable<LectureProgressResponse> {
    return this.http.put<LectureProgressResponse>(
      `${this.apiUrl}/me/enrollments/${enrollmentId}/lectures/${lectureId}/progress`,
      request
    );
  }

  enrollInCourse(courseId: number): Observable<EnrollmentResponse> {
  return this.http.post<EnrollmentResponse>(
    `${this.apiUrl}/courses/${courseId}/enroll`,
    {}
  );
}

}