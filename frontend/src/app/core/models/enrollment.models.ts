export interface EnrollmentResponse {
  id: number;
  courseId: number;
  courseTitle: string;
  courseSlug: string;
  thumbnailUrl: string | null;
  status: 'ACTIVE' | 'IN_PROGRESS' | 'COMPLETED' | string;
  enrolledAt: string;
  completedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateLectureProgressRequest {
  lastPositionSeconds: number;
  completed: boolean;
}

export interface LectureProgressResponse {
  id: number;
  lectureId: number;
  lectureTitle: string;
  moduleId: number;
  moduleTitle: string;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  lastPositionSeconds: number;
  durationSeconds: number | null;
  completedAt: string | null;
  updatedAt: string;
}

export interface CourseProgressResponse {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  enrollmentStatus: string;
  totalLectures: number;
  completedLectures: number;
  progressPercentage: number;
}