import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, switchMap } from 'rxjs';

import {
  LectureResponse,
  LearningModule,
  LearningWorkspaceData,
  ModuleResponse
} from '../models/curriculum.models';

@Injectable({
  providedIn: 'root'
})
export class CurriculumService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  getCourseCurriculum(
    courseId: number
  ): Observable<LearningWorkspaceData> {

    return this.http
      .get<ModuleResponse[]>(
        `${this.apiUrl}/courses/${courseId}/curriculum`
      )
      .pipe(
        switchMap((modules) => {
          const sortedModules = [...modules].sort(
            (a, b) => a.sequenceNumber - b.sequenceNumber
          );

          if (sortedModules.length === 0) {
            return [
              {
                courseId,
                courseTitle: 'Course',
                modules: []
              } satisfies LearningWorkspaceData
            ];
          }

          const moduleRequests = sortedModules.map((module) =>
            this.getModuleLectures(module.id).pipe(
              map((lectures): LearningModule => ({
                ...module,
                lectures: [...lectures].sort(
                  (a, b) => a.sequenceNumber - b.sequenceNumber
                )
              }))
            )
          );

          return forkJoin(moduleRequests).pipe(
            map((learningModules) => ({
              courseId,
              courseTitle: sortedModules[0].courseTitle,
              modules: learningModules
            }))
          );
        })
      );
  }

  getModuleLectures(
    moduleId: number
  ): Observable<LectureResponse[]> {
    return this.http.get<LectureResponse[]>(
      `${this.apiUrl}/modules/${moduleId}/lectures`
    );
  }

  getLecture(lectureId: number): Observable<LectureResponse> {
    return this.http.get<LectureResponse>(
      `${this.apiUrl}/lectures/${lectureId}`
    );
  }
}