export interface CourseResponse {
  id: number;
  title: string;
  slug: string;
  description: string | null;
  examId: number | null;
  examName: string | null;
  instructorName: string | null;
  difficulty: string | null;
  language: string | null;
  thumbnailUrl: string | null;
  youtubePlaylistId: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CoursePageResponse {
  content: CourseResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}