
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html'
})
export class Register {
  readonly name = signal('');
  readonly email = signal('');
  readonly password = signal('');
  readonly confirmPassword = signal('');

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly showPassword = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  submitRegistration(): void {
    this.errorMessage.set('');
    this.successMessage.set('');

    const name = this.name().trim();
    const email = this.email().trim();
    const password = this.password();

    if (!name || !email || !password || !this.confirmPassword()) {
      this.errorMessage.set('Please complete all required fields.');
      return;
    }

    if (name.length > 100) {
      this.errorMessage.set('Name must not exceed 100 characters.');
      return;
    }

    if (email.length > 254) {
      this.errorMessage.set('Email must not exceed 254 characters.');
      return;
    }

    if (password.length < 8 || password.length > 72) {
      this.errorMessage.set('Password must be between 8 and 72 characters.');
      return;
    }

    if (password !== this.confirmPassword()) {
      this.errorMessage.set('Passwords do not match.');
      return;
    }

    this.loading.set(true);

    this.authService.register({
      name,
      email,
      password
    })
    .pipe(finalize(() => this.loading.set(false)))
    .subscribe({
      next: response => {
        this.successMessage.set(
          response.message || 'Registration successful. Please sign in.'
        );

        // Registration does not issue a JWT.
        // Redirect to login after displaying the success message.
        setTimeout(() => {
          this.router.navigate(['/auth/login'], {
            queryParams: { registered: 'true' }
          });
        }, 1200);
      },
      error: error => {
        if (error.status === 0) {
          this.errorMessage.set(
            'Unable to connect to the server. Please try again.'
          );
        } else if (error.status === 400) {
          this.errorMessage.set(
            error.error?.message ||
            'Please check your name, email, and password.'
          );
        } else if (error.status === 409) {
          this.errorMessage.set(
            'An account with this email already exists.'
          );
        } else {
          this.errorMessage.set(
            error.error?.message ||
            'Registration failed. Please try again.'
          );
        }
      }
    });
  }

  togglePassword(): void {
    this.showPassword.update(value => !value);
  }
}
