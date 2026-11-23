export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  name: string;
  email: string;
  role: string;
  accessToken: string;
  message: string;
}

export interface RegisterResponse {
  id: number;
  name: string;
  email: string;
  role: string;
  accessToken: string | null;
  message: string;
}

export interface CurrentUser {
  id: number;
  name: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface UserProfile {
  id: number;
  userId: number;
  educationLevel: string | null;
  academicStream: string | null;
  learningGoal: string | null;
  skillLevel: string | null;
  preparationStage: string | null;
  preferredLanguage: string | null;
  preferredMode: string | null;
  onboardingCompleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  educationLevel: string;
  academicStream: string | null;
  learningGoal: string;
  skillLevel: string;
  preparationStage: string | null;
  preferredLanguage: string;
  preferredMode: string;
}