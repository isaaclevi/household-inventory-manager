import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

const API = 'http://localhost:8080/api';
const STORAGE_KEY = 'inventory.auth';

export interface AuthResponse {
  token: string;
  username: string;
  displayName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private auth = signal<AuthResponse | null>(this.restore());

  readonly user = computed(() => this.auth());
  readonly isLoggedIn = computed(() => this.auth() !== null);

  get token(): string | null {
    return this.auth()?.token ?? null;
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/login`, { username, password })
      .pipe(tap(res => this.store(res)));
  }

  register(username: string, password: string, displayName: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/register`, { username, password, displayName })
      .pipe(tap(res => this.store(res)));
  }

  logout(): void {
    this.auth.set(null);
    localStorage.removeItem(STORAGE_KEY);
    this.router.navigate(['/login']);
  }

  private store(res: AuthResponse): void {
    this.auth.set(res);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(res));
  }

  private restore(): AuthResponse | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) as AuthResponse : null;
    } catch {
      return null;
    }
  }
}
