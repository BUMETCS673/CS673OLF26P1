import { Component } from '@angular/core';
import { UsersComponent } from '../../../components/users/users.component';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [UsersComponent],
  template: '<app-users />',
})
export class UsersPage {}