
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

type BookmarkType = 'video' | 'quiz';

interface SavedLecture {
  id: number;
  courseId: number;
  title: string;
  courseTitle: string;
  moduleTitle: string;
  duration: string;
  type: BookmarkType;
  savedDate: string;
}

@Component({
  selector: 'app-bookmarks',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './bookmarks.html'
})
export class Bookmarks {
  readonly searchQuery = signal('');
  readonly selectedType = signal('all');

  readonly savedLectures = signal<SavedLecture[]>([
    {
      id: 1,
      courseId: 1,
      title: 'Introduction to Java',
      courseTitle: 'Complete Java Programming',
      moduleTitle: 'Module 1: Introduction',
      duration: '12 min',
      type: 'video',
      savedDate: 'Recently saved'
    },
    {
      id: 2,
      courseId: 1,
      title: 'Classes and Objects',
      courseTitle: 'Complete Java Programming',
      moduleTitle: 'Module 3: Object-Oriented Programming',
      duration: '25 min',
      type: 'video',
      savedDate: 'Recently saved'
    },
    {
      id: 3,
      courseId: 1,
      title: 'Module 1 Knowledge Check',
      courseTitle: 'Complete Java Programming',
      moduleTitle: 'Module 1: Introduction',
      duration: '10 min',
      type: 'quiz',
      savedDate: 'Recently saved'
    },
    {
      id: 4,
      courseId: 2,
      title: 'Understanding Arrays',
      courseTitle: 'Data Structures & Algorithms',
      moduleTitle: 'Module 2: Arrays',
      duration: '18 min',
      type: 'video',
      savedDate: 'Recently saved'
    }
  ]);

  readonly filteredBookmarks = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const type = this.selectedType();

    return this.savedLectures().filter(item => {
      const matchesSearch =
        item.title.toLowerCase().includes(query) ||
        item.courseTitle.toLowerCase().includes(query) ||
        item.moduleTitle.toLowerCase().includes(query);

      const matchesType = type === 'all' || item.type === type;

      return matchesSearch && matchesType;
    });
  });

  readonly videoCount = computed(
    () => this.savedLectures().filter(item => item.type === 'video').length
  );

  readonly quizCount = computed(
    () => this.savedLectures().filter(item => item.type === 'quiz').length
  );

  removeBookmark(id: number): void {
    this.savedLectures.update(items =>
      items.filter(item => item.id !== id)
    );
  }

  clearBookmarks(): void {
    this.savedLectures.set([]);
  }

  resetFilters(): void {
    this.searchQuery.set('');
    this.selectedType.set('all');
  }
}
