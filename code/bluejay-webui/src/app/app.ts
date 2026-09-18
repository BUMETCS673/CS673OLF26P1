// AI-ASSISTED: NO
// Tool: Angular CLI scaffold
// Prompt Summary: "N/A"
// AI Contribution: None

import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('bluejay-webui');
}
