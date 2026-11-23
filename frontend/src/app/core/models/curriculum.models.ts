export interface CourseResponse {
  id: number;
  title: string;
  slug: string;
  description: string | null;
  examId: number;
  examName: string;
  instructorName: string | null;
  difficulty: string;
  language: string;
  thumbnailUrl: string | null;
  youtubePlaylistId: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ModuleResponse {
  id: number;
  courseId: number;
  courseTitle: string;
  title: string;
  description: string | null;
  sequenceNumber: number;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LectureResponse {
  id: number;
  moduleId: number;
  moduleTitle: string;
  courseId: number;
  title: string;
  description: string | null;
  youtubeVideoId: string | null;
  durationSeconds: number | null;
  sequenceNumber: number;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LearningModule extends ModuleResponse {
  lectures: LectureResponse[];
}

export interface LearningWorkspaceData {
  courseId: number;
  courseTitle: string;
  modules: LearningModule[];
}