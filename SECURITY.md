# Security Policy

## Supported Versions

AGC EasyDoc is an open-source Android application. Security updates and bug fixes are provided for the latest stable release on the `main` branch.

| Version | Supported          |
| ------- | ------------------ |
| Latest  | :white_check_mark: |
| < Latest| :x:                |

## Reporting a Vulnerability

We take the security of AGC EasyDoc seriously. If you discover a security vulnerability, please report it privately rather than opening a public issue.

- **Email/Contact:** Please report security vulnerabilities by creating a confidential security advisory or contacting the maintainers directly through repository channels.
- **Information to Include:**
  - Description of the vulnerability and steps to reproduce.
  - Potential impact and affected components (e.g., Room database, PDF export, UI input handling).
  - Any proof-of-concept or exploit code if applicable.

We will acknowledge receipt of your vulnerability report within 48 hours and work with you to coordinate disclosure and remediation.

## Security Design & Privacy

AGC EasyDoc is designed with a strong privacy-first architecture:
- **Local-First Storage:** All documents, drafts, tables, and signature blocks are stored locally on your device using Android Room.
- **No Telemetry or Tracking:** The application does not include analytics SDKs, tracking pixels, crash reporting telemetry, or cloud synchronization endpoints.
- **Offline Operation:** The app functions entirely offline without requiring user accounts or network access.
- **File & Storage Permissions:** Exported PDFs are securely written to the standard Android Downloads collection using Scoped Storage guidelines.
