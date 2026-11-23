
import { Component, computed, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface QuizQuestion {
  id: number;
  question: string;
  options: string[];
  correctAnswer: number;
  explanation: string;
}

@Component({
  selector: 'app-quiz-attempt',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './quiz-attempt.html'
})
export class QuizAttempt implements OnInit, OnDestroy {
  readonly quizTitle = 'Java Programming Fundamentals';
  readonly durationMinutes = 10;

  readonly questions: QuizQuestion[] = [
    {
      id: 1,
      question: 'Which keyword is used to create a class in Java?',
      options: ['function', 'class', 'struct', 'define'],
      correctAnswer: 1,
      explanation: 'The class keyword is used to declare a class in Java.'
    },
    {
      id: 2,
      question: 'Which of these is not a primitive data type in Java?',
      options: ['int', 'boolean', 'String', 'char'],
      correctAnswer: 2,
      explanation: 'String is a class in Java, not a primitive data type.'
    },
    {
      id: 3,
      question: 'Which method is the entry point of a standard Java application?',
      options: ['start()', 'run()', 'main()', 'execute()'],
      correctAnswer: 2,
      explanation: 'The main() method is the standard entry point of a Java application.'
    },
    {
      id: 4,
      question: 'Which concept allows a class to inherit properties from another class?',
      options: ['Encapsulation', 'Inheritance', 'Abstraction', 'Polymorphism'],
      correctAnswer: 1,
      explanation: 'Inheritance allows a class to acquire properties and methods from another class.'
    },
    {
      id: 5,
      question: 'Which operator is used to compare two values for equality?',
      options: ['=', '!=', '==', '&&'],
      correctAnswer: 2,
      explanation: 'The == operator compares two values for equality.'
    }
  ];

  readonly currentIndex = signal(0);
  readonly selectedAnswers = signal<Record<number, number>>({});
  readonly submitted = signal(false);
  readonly remainingSeconds = signal(this.durationMinutes * 60);

  private timerId?: ReturnType<typeof setInterval>;

  readonly currentQuestion = computed(
    () => this.questions[this.currentIndex()]
  );

  readonly answeredCount = computed(
    () => Object.keys(this.selectedAnswers()).length
  );

  readonly progress = computed(
    () => (this.answeredCount() / this.questions.length) * 100
  );

  readonly score = computed(() =>
    this.questions.reduce((total, question, index) => {
      return total +
        (this.selectedAnswers()[index] === question.correctAnswer ? 1 : 0);
    }, 0)
  );

  readonly percentage = computed(() =>
    Math.round((this.score() / this.questions.length) * 100)
  );

  ngOnInit(): void {
    this.timerId = setInterval(() => {
      if (this.submitted()) {
        this.stopTimer();
        return;
      }

      if (this.remainingSeconds() <= 1) {
        this.remainingSeconds.set(0);
        this.submitQuiz();
        return;
      }

      this.remainingSeconds.update(seconds => seconds - 1);
    }, 1000);
  }

  ngOnDestroy(): void {
    this.stopTimer();
  }

  selectAnswer(optionIndex: number): void {
    if (this.submitted()) return;

    const questionIndex = this.currentIndex();

    this.selectedAnswers.update(answers => ({
      ...answers,
      [questionIndex]: optionIndex
    }));
  }

  isSelected(optionIndex: number): boolean {
    return this.selectedAnswers()[this.currentIndex()] === optionIndex;
  }

  goToQuestion(index: number): void {
    if (index < 0 || index >= this.questions.length || this.submitted()) return;
    this.currentIndex.set(index);
  }

  nextQuestion(): void {
    this.goToQuestion(this.currentIndex() + 1);
  }

  previousQuestion(): void {
    this.goToQuestion(this.currentIndex() - 1);
  }

  submitQuiz(): void {
    if (this.submitted()) return;

    this.submitted.set(true);
    this.stopTimer();
  }

  formatTime(): string {
    const minutes = Math.floor(this.remainingSeconds() / 60);
    const seconds = this.remainingSeconds() % 60;

    return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
  }

  private stopTimer(): void {
    if (this.timerId) {
      clearInterval(this.timerId);
      this.timerId = undefined;
    }
  }
}
