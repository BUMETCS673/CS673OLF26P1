# BluejayWebui

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.24.

## Prerequisites & Version Requirements

Ensure your environment meets the following version requirements before setting up the application:

* **Node.js:** `v20.19.0+`, `v22.12.0+`, or `>= v24.0.0`
* **npm:** `>= 8.0.0`
* **Angular CLI:** `v21.2.24` (matches project framework release)

---

## First-Time Environment Setup

### 1. Verify Node.js & npm Versions
Check your current installed versions:
```bash
node -v
npm -v
```

If your Node.js version is below `v20.19.0`, upgrade it before proceeding:

* **macOS (via `n` tool):**
```bash
sudo npm install -g n
sudo n lts
```


* **macOS (via Homebrew):**
```bash
brew update && brew upgrade node
```


* **Windows:** Download and run the latest LTS installer from [nodejs.org](https://nodejs.org/?utm_source=gemini).

### 2. Install Angular CLI Globally

To run `ng` commands directly in your terminal, install version 21 globally:

```bash
npm install -g @angular/cli@21
```

### 3. Install Project Dependencies

Navigate to the root directory of this project and install the dependencies:

```bash
npm install
```

---

## Development Server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

> **Note:** If you prefer not to install the Angular CLI globally, you can start the development server using:
> ```bash
> npx ng serve
> ```

## Code Scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running Unit Tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running End-to-End Tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
