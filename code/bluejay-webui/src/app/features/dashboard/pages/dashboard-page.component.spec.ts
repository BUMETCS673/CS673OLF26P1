import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { AuthService } from '../../../core/auth/auth.service';
import { IdleTimerService } from '../../../core/auth/idle-timer.service';
import { DashboardPage } from './dashboard-page.component';

describe('DashboardPage', () => {
  const authServiceMock = { getUsername: vi.fn() };
  const idleTimerMock = { start: vi.fn(), stop: vi.fn() };

  beforeEach(() => {
    authServiceMock.getUsername.mockReset();
    authServiceMock.getUsername.mockReturnValue('cashier');
    idleTimerMock.start.mockReset();
    idleTimerMock.stop.mockReset();

    TestBed.configureTestingModule({
      imports: [DashboardPage],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: IdleTimerService, useValue: idleTimerMock },
      ],
    });
  });

  it('should start the idle timer on init and stop it on destroy', () => {
    const fixture = TestBed.createComponent(DashboardPage);
    fixture.detectChanges();
    expect(idleTimerMock.start).toHaveBeenCalledTimes(1);

    fixture.destroy();
    expect(idleTimerMock.stop).toHaveBeenCalledTimes(1);
  });

  it('should not render a logout control because it is in the application header', () => {
    const fixture = TestBed.createComponent(DashboardPage);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('button')).toBeNull();
  });
});