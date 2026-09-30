import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { IdleTimerService } from '../../../core/auth/idle-timer.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  template: `
    <section class="dashboard-page">
      <h1>Welcome{{ username ? ', ' + username : '' }}!</h1>
      <p>Welcome to your secure dashboard.</p>
    </section>
  `,
  styles: [
    `
      .dashboard-page {
        padding: 24px;
        text-align: center;
      }
    `,
  ],
})
export class DashboardPage implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private idleTimer = inject(IdleTimerService);
  username = this.authService.getUsername();

  ngOnInit(): void {
    this.idleTimer.start();
  }

  ngOnDestroy(): void {
    this.idleTimer.stop();
  }

}