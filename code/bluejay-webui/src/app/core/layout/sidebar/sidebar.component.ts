import { Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NAV_ITEMS } from './navigation.model';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [MatIconModule, MatListModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  private readonly authService = inject(AuthService);

  get navItems() {
    return NAV_ITEMS.filter((item) => {
      // Show item if no roles are required
      if (!item.roles || item.roles.length === 0) {
        return true;
      }
      // Show item if the user has at least one of the allowed roles
      return item.roles.some((role) => this.authService.isRole(role));
    });
  }
}
