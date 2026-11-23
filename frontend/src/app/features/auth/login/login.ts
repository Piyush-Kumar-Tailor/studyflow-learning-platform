
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html'
})
export class Login {
  readonly email = signal('');
  readonly password = signal('');
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly showPassword = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  submitLogin(): void {
    const email = this.email().trim();
    const password = this.password();

    this.errorMessage.set('');

    if (!email || !password) {
      this.errorMessage.set('Please enter your email and password.');
      return;
    }

    this.loading.set(true);

    this.authService.login({ email, password })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: response => {
          const role = response.role
            .toUpperCase()
            .replace('ROLE_', '');

          if (role === 'ADMIN') {
            this.router.navigateByUrl('/admin/dashboard');
          } else {
            this.router.navigateByUrl('/student/dashboard');
          }
        },
        error: error => {
          if (error.status === 401 || error.status === 403) {
            this.errorMessage.set('Invalid email or password.');
          } else if (error.status === 0) {
            this.errorMessage.set(
              'Unable to connect to the server. Check whether the backend is running.'
            );
          } else {
            this.errorMessage.set(
              error.error?.message || 'Login failed. Please try again.'
            );
          }
        }
      });
  }

  togglePassword(): void {
    this.showPassword.update(value => !value);
  }
}
