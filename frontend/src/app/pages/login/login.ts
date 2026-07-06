import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);

  mode = signal<'login' | 'register'>('login');
  error = signal('');
  busy = signal(false);

  username = '';
  password = '';
  displayName = '';

  toggleMode(): void {
    this.mode.set(this.mode() === 'login' ? 'register' : 'login');
    this.error.set('');
  }

  submit(): void {
    if (!this.username.trim() || !this.password) return;
    this.busy.set(true);
    this.error.set('');

    const request = this.mode() === 'login'
      ? this.auth.login(this.username.trim(), this.password)
      : this.auth.register(this.username.trim(), this.password, this.displayName.trim() || this.username.trim());

    request.subscribe({
      next: () => this.router.navigate(['/']),
      error: err => {
        this.busy.set(false);
        if (err.status === 401) this.error.set('Wrong username or password');
        else if (err.status === 409) this.error.set('That username is already taken');
        else if (err.status === 400) this.error.set('Password must be at least 8 characters');
        else this.error.set('Something went wrong — is the backend running?');
      }
    });
  }
}
