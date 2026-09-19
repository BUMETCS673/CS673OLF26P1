// AI-ASSISTED: NO
// Tool: Angular CLI scaffold
// Prompt Summary: "N/A"
// AI Contribution: None


import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
