
import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { LoginRequest, LoginResponse, CurrentUser, RegisterRequest, RegisterResponse, UserProfile, UpdateProfileRequest } from '../models/auth.models';


@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly apiUrl = 'http://localhost:8080/api/v1';
    private readonly tokenKey = 'studyflow-access-token';

    private readonly userState = signal<CurrentUser | null>(null);

    readonly currentUser = this.userState.asReadonly();
    readonly isAuthenticated = computed(() => !!this.getToken());

    constructor(private http: HttpClient) { }

    login(credentials: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
            `${this.apiUrl}/auth/login`,
            credentials
        ).pipe(
            tap(response => {
                localStorage.setItem(this.tokenKey, response.accessToken);

                // The login response does not include createdAt.
                // Load the complete user profile separately when needed.
                this.userState.set({
                    id: response.id,
                    name: response.name,
                    email: response.email,
                    role: response.role,
                    createdAt: ''
                });
            })
        );
    }

    loadCurrentUser(): Observable<CurrentUser> {
        return this.http.get<CurrentUser>(
            `${this.apiUrl}/me`
        ).pipe(
            tap(user => this.userState.set(user)),
            catchError(error => {
                this.logout();
                return throwError(() => error);
            })
        );
    }



    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }

    logout(): void {
        localStorage.removeItem(this.tokenKey);
        this.userState.set(null);
    }

    getUserProfile(): Observable<UserProfile> {
        return this.http.get<UserProfile>(
            `${this.apiUrl}/me/profile`
        );
    }

    register(data: RegisterRequest): Observable<RegisterResponse> {
        return this.http.post<RegisterResponse>(
            `${this.apiUrl}/auth/register`,
            data
        );
    }

    updateUserProfile(
        data: UpdateProfileRequest
    ): Observable<UserProfile> {
        return this.http.put<UserProfile>(
            `${this.apiUrl}/me/profile`,
            data
        );
    }
}
