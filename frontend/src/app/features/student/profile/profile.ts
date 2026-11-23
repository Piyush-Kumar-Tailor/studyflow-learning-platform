
import { Component, signal, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { UserProfile, UpdateProfileRequest } from '../../../core/models/auth.models';


interface StudentProfile {
  fullName: string;
  email: string;
  phone: string;
  college: string;
  course: string;
  year: string;
  bio: string;
}



@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile.html'
})
export class Profile {
  readonly profile = signal<StudentProfile>({
    fullName: 'Student Name',
    email: 'student@example.com',
    phone: '',
    college: '',
    course: 'Computer Science Engineering',
    year: '3rd Year',
    bio: 'I am focused on learning, improving my technical skills, and building practical projects.'
  });

  profileForm: UpdateProfileRequest = {
    educationLevel: '',
    academicStream: null,
    learningGoal: '',
    skillLevel: '',
    preparationStage: null,
    preferredLanguage: '',
    preferredMode: ''
  };

  savingProfile = signal(false);
  saveMessage = signal('');
  saveError = signal('');
  editingAcademic = signal(false);
  academicSaveMessage = signal('');
  academicSaveError = signal('');

  readonly educationLevels = [
    { value: 'CLASS_8_OR_BELOW', label: 'Class 8 or below' },
    { value: 'CLASS_10', label: 'Class 10' },
    { value: 'CLASS_12', label: 'Class 12' },
    { value: 'DIPLOMA', label: 'Diploma' },
    { value: 'UNDERGRADUATE', label: 'Undergraduate' },
    { value: 'POSTGRADUATE', label: 'Postgraduate' },
    { value: 'DOCTORAL', label: 'Doctoral' },
    { value: 'WORKING_PROFESSIONAL', label: 'Working professional' },
    { value: 'OTHER', label: 'Other' }
  ];

  readonly academicStreams = [
    { value: 'SCIENCE_PCM', label: 'Science (PCM)' },
    { value: 'SCIENCE_PCB', label: 'Science (PCB)' },
    { value: 'SCIENCE_PCMB', label: 'Science (PCMB)' },
    { value: 'COMMERCE', label: 'Commerce' },
    { value: 'ARTS_HUMANITIES', label: 'Arts & Humanities' },
    { value: 'ENGINEERING_TECH', label: 'Engineering & Technology' },
    { value: 'COMPUTER_SCIENCE_IT', label: 'Computer Science & IT' },
    { value: 'MEDICAL_HEALTH', label: 'Medical & Healthcare' },
    { value: 'PHARMACY', label: 'Pharmacy' },
    { value: 'AGRICULTURE', label: 'Agriculture' },
    { value: 'LAW', label: 'Law' },
    { value: 'MANAGEMENT', label: 'Management' },
    { value: 'EDUCATION', label: 'Education' },
    { value: 'DESIGN', label: 'Design' },
    { value: 'VOCATIONAL', label: 'Vocational' },
    { value: 'OTHER', label: 'Other' }
  ];

  readonly learningGoals = [
    { value: 'SCHOOL_ACADEMICS', label: 'School academics' },
    { value: 'COLLEGE_ACADEMICS', label: 'College academics' },
    { value: 'ENGINEERING_ENTRANCE_PREPARATION', label: 'Engineering entrance preparation' },
    { value: 'MEDICAL_ENTRANCE_PREPARATION', label: 'Medical entrance preparation' },
    { value: 'OTHER_ENTRANCE_PREPARATION', label: 'Other entrance preparation' },
    { value: 'STATE_GOVERNMENT_JOB', label: 'State government jobs' },
    { value: 'CENTRAL_GOVERNMENT_JOB', label: 'Central government jobs' },
    { value: 'RAILWAY_JOB', label: 'Railway jobs' },
    { value: 'BANKING_JOB', label: 'Banking jobs' },
    { value: 'TEACHING_JOB', label: 'Teaching jobs' },
    { value: 'DEFENCE_JOB', label: 'Defence jobs' },
    { value: 'POLICE_JOB', label: 'Police jobs' },
    { value: 'TECHNICAL_SKILL_DEVELOPMENT', label: 'Technical skill development' },
    { value: 'SOFTWARE_DEVELOPER_CAREER', label: 'Software developer career' },
    { value: 'AI_ML_CAREER', label: 'AI & ML career' },
    { value: 'DATA_SCIENCE_CAREER', label: 'Data science career' },
    { value: 'CYBERSECURITY_CAREER', label: 'Cybersecurity career' },
    { value: 'PLACEMENT_PREPARATION', label: 'Placement preparation' },
    { value: 'INTERVIEW_PREPARATION', label: 'Interview preparation' },
    { value: 'PROFESSIONAL_CERTIFICATION', label: 'Professional certification' },
    { value: 'STUDY_ABROAD', label: 'Study abroad' },
    { value: 'PERSONAL_LEARNING', label: 'Personal learning' },
    { value: 'OTHER', label: 'Other' }
  ];

  readonly skillLevels = [
    { value: 'COMPLETE_BEGINNER', label: 'Complete beginner' },
    { value: 'BEGINNER', label: 'Beginner' },
    { value: 'INTERMEDIATE', label: 'Intermediate' },
    { value: 'ADVANCED', label: 'Advanced' }
  ];

  readonly preparationStages = [
    { value: 'BEGINNER', label: 'Just starting' },
    { value: 'INTERMEDIATE', label: 'Intermediate preparation' },
    { value: 'REPEATER', label: 'Repeater' },
    { value: 'TEST_SERIES', label: 'Test series & practice' }
  ];

  readonly languages = [
    { value: 'ENGLISH', label: 'English' },
    { value: 'HINDI', label: 'Hindi' },
    { value: 'HINGLISH', label: 'Hinglish' },
    { value: 'REGIONAL', label: 'Regional language' }
  ];

  readonly preferredModes = [
    { value: 'SELF_PACED_RECORDED', label: 'Self-paced recorded learning' },
    { value: 'LIVE_ONLINE', label: 'Live online learning' },
    { value: 'HYBRID', label: 'Hybrid learning' },
    { value: 'OFFLINE', label: 'Offline learning' }
  ];

  readonly editing = signal(false);
  readonly authService = inject(AuthService);
  readonly profileLoading = signal(true);
  readonly profileError = signal('');
  readonly userProfile = signal<UserProfile | null>(null);
  ngOnInit(): void {
    this.loadAcademicProfile();
  }

  loadAcademicProfile(): void {
    this.profileLoading.set(true);
    this.profileError.set('');

    this.authService.getUserProfile().subscribe({
      next: (profile) => {
        this.userProfile.set(profile);

        this.profileForm = {
          educationLevel: profile.educationLevel ?? '',
          academicStream: profile.academicStream,
          learningGoal: profile.learningGoal ?? '',
          skillLevel: profile.skillLevel ?? '',
          preparationStage: profile.preparationStage,
          preferredLanguage: profile.preferredLanguage ?? '',
          preferredMode: profile.preferredMode ?? ''
        };

        this.profileLoading.set(false);
      },
      error: error => {
        this.profileLoading.set(false);

        this.profileError.set(
          error.status === 404
            ? 'Your academic profile has not been created yet.'
            : 'Unable to load your academic profile. Please try again.'
        );
      }
    });
  }

  saveAcademicProfile(): void {
    this.academicSaveMessage.set('');
    this.academicSaveError.set('');

    const data: UpdateProfileRequest = {
      ...this.profileForm,
      academicStream: this.profileForm.academicStream || null,
      preparationStage: this.profileForm.preparationStage || null
    };

    if (
      !data.educationLevel ||
      !data.learningGoal ||
      !data.skillLevel ||
      !data.preferredLanguage ||
      !data.preferredMode
    ) {
      this.academicSaveError.set('Please complete all required fields.');
      return;
    }

    this.savingProfile.set(true);

    this.authService.updateUserProfile(data).subscribe({
      next: (profile) => {
        this.userProfile.set(profile);

        this.profileForm = {
          educationLevel: profile.educationLevel ?? '',
          academicStream: profile.academicStream,
          learningGoal: profile.learningGoal ?? '',
          skillLevel: profile.skillLevel ?? '',
          preparationStage: profile.preparationStage,
          preferredLanguage: profile.preferredLanguage ?? '',
          preferredMode: profile.preferredMode ?? ''
        };

        this.savingProfile.set(false);
        this.editingAcademic.set(false);
        this.academicSaveMessage.set('Your academic profile has been updated.');
      },
      error: (error) => {
        this.savingProfile.set(false);
        this.academicSaveError.set(
          error?.error?.message ??
          'Unable to save your academic profile. Please try again.'
        );
      }
    });
  }

  startAcademicEditing(): void {
    const profile = this.userProfile();

    if (!profile) return;

    this.profileForm = {
      educationLevel: profile.educationLevel ?? '',
      academicStream: profile.academicStream,
      learningGoal: profile.learningGoal ?? '',
      skillLevel: profile.skillLevel ?? '',
      preparationStage: profile.preparationStage,
      preferredLanguage: profile.preferredLanguage ?? '',
      preferredMode: profile.preferredMode ?? ''
    };

    this.academicSaveMessage.set('');
    this.academicSaveError.set('');
    this.editingAcademic.set(true);
  }

  cancelAcademicEditing(): void {
    const profile = this.userProfile();

    if (profile) {
      this.profileForm = {
        educationLevel: profile.educationLevel ?? '',
        academicStream: profile.academicStream,
        learningGoal: profile.learningGoal ?? '',
        skillLevel: profile.skillLevel ?? '',
        preparationStage: profile.preparationStage,
        preferredLanguage: profile.preferredLanguage ?? '',
        preferredMode: profile.preferredMode ?? ''
      };
    }

    this.academicSaveError.set('');
    this.editingAcademic.set(false);
  }

  readonly preferences = signal({
    emailNotifications: true,
    learningReminders: true,
    weeklyProgress: false
  });

  readonly displayName = signal('Student Name');

  startEditing(): void {
    this.displayName.set(this.profile().fullName);
    this.editing.set(true);
    this.saveMessage.set('');
  }

  getAcademicLabel(
    options: { value: string; label: string }[],
    value: string | null | undefined
  ): string {
    return options.find(item => item.value === value)?.label ?? 'Not selected';
  }

  saveProfile(): void {
    const name = this.displayName().trim();

    if (!name) return;

    this.profile.update(profile => ({
      ...profile,
      fullName: name
    }));

    this.editing.set(false);
    this.saveMessage.set('Profile updated successfully.');
  }

  cancelEditing(): void {
    this.displayName.set(this.profile().fullName);
    this.editing.set(false);
  }

  updateField(field: keyof StudentProfile, value: string): void {
    this.profile.update(profile => ({
      ...profile,
      [field]: value
    }));
  }

  updatePreference(
    key: keyof typeof this.preferences extends never ? never : 'emailNotifications' | 'learningReminders' | 'weeklyProgress',
    checked: boolean
  ): void {
    this.preferences.update(current => ({
      ...current,
      [key]: checked
    }));
  }

  get initials(): string {
    return this.profile().fullName
      .trim()
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part[0].toUpperCase())
      .join('');
  }
}
