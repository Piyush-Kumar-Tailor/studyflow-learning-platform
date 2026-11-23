
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface StudyNote {
  id: number;
  title: string;
  content: string;
  category: string;
  updatedAt: string;
}

@Component({
  selector: 'app-notes',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './notes.html'
})
export class Notes {
  readonly notes = signal<StudyNote[]>([
    {
      id: 1,
      title: 'Java OOP Concepts',
      content: 'Encapsulation: wrapping data and methods into a single unit.\nInheritance: acquiring properties from another class.\nPolymorphism: one interface with multiple implementations.\nAbstraction: hiding implementation details.',
      category: 'Programming',
      updatedAt: 'Sample note'
    },
    {
      id: 2,
      title: 'DBMS Important Topics',
      content: 'Database normalization\nPrimary key and foreign key\nACID properties\nSQL joins\nTransactions and concurrency control',
      category: 'University',
      updatedAt: 'Sample note'
    },
    {
      id: 3,
      title: 'DSA Revision Checklist',
      content: 'Arrays and Strings\nLinked Lists\nStacks and Queues\nTrees and Graphs\nSorting and Searching\nDynamic Programming',
      category: 'Revision',
      updatedAt: 'Sample note'
    }
  ]);

  readonly searchQuery = signal('');
  readonly selectedCategory = signal('All');
  readonly editorOpen = signal(false);
  readonly editingNoteId = signal<number | null>(null);

  readonly noteTitle = signal('');
  readonly noteContent = signal('');
  readonly noteCategory = signal('Programming');

  readonly categories = ['Programming', 'University', 'Revision', 'Other'];

  readonly filteredNotes = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const category = this.selectedCategory();

    return this.notes().filter(note => {
      const matchesQuery =
        note.title.toLowerCase().includes(query) ||
        note.content.toLowerCase().includes(query);

      const matchesCategory =
        category === 'All' || note.category === category;

      return matchesQuery && matchesCategory;
    });
  });

  openNewNote(): void {
    this.editingNoteId.set(null);
    this.noteTitle.set('');
    this.noteContent.set('');
    this.noteCategory.set('Programming');
    this.editorOpen.set(true);
  }

  editNote(note: StudyNote): void {
    this.editingNoteId.set(note.id);
    this.noteTitle.set(note.title);
    this.noteContent.set(note.content);
    this.noteCategory.set(note.category);
    this.editorOpen.set(true);
  }

  saveNote(): void {
    const title = this.noteTitle().trim();
    const content = this.noteContent().trim();

    if (!title || !content) return;

    const editingId = this.editingNoteId();

    if (editingId !== null) {
      this.notes.update(items =>
        items.map(note =>
          note.id === editingId
            ? {
                ...note,
                title,
                content,
                category: this.noteCategory(),
                updatedAt: 'Just updated'
              }
            : note
        )
      );
    } else {
      const newNote: StudyNote = {
        id: Date.now(),
        title,
        content,
        category: this.noteCategory(),
        updatedAt: 'Just created'
      };

      this.notes.update(items => [newNote, ...items]);
    }

    this.closeEditor();
  }

  deleteNote(id: number): void {
    this.notes.update(items => items.filter(note => note.id !== id));
  }

  closeEditor(): void {
    this.editorOpen.set(false);
    this.editingNoteId.set(null);
  }

  resetFilters(): void {
    this.searchQuery.set('');
    this.selectedCategory.set('All');
  }

  getPreview(content: string): string {
    return content.length > 150
      ? content.slice(0, 150) + '...'
      : content;
  }
}
