import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CoursePageResponse, CourseResponse } from '../models/course.models';

@Injectable({
  providedIn: 'root'
})
export class CourseCatalogService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/v1';

  getCourses(
    page: number = 0,
    size: number = 12
  ): Observable<CoursePageResponse> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<CoursePageResponse>(
      `${this.apiUrl}/courses`,
      { params }
    );
  }

  getCourseBySlug(slug: string): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(
      `${this.apiUrl}/courses/${encodeURIComponent(slug)}`
    );
  }
}