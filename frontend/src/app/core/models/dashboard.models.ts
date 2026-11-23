export interface StudentDashboardResponse {
  totalEnrolledCourses: number;
  inProgressCourses: number;
  completedCourses: number;
  totalLecturesCompleted: number;
  recentCourses: RecentCourseProgress[];
  continueLearning: ContinueLearningResponse | null;
}

export interface RecentCourseProgress {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  status: string;
  totalLectures: number;
  completedLectures: number;
  progressPercentage: number;
}

export interface ContinueLearningResponse {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  lectureId: number;
  lectureTitle: string;
  youtubeVideoId: string;
  lastPositionSeconds: number;
  lectureStatus: string;
}