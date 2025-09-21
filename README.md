# Recruiting Platform

This project is a Spring Boot-based recruiting platform with RESTful APIs for user, employer, job, and file management. It is designed to run behind Nginx and uses MySQL for data storage.

## Features

- **User Registration & Authentication**
  - Register as a job seeker or employer
  - Login and JWT-based authentication
  - Password recovery endpoint (`POST /api/auth/recover`)

- **Profile Management**
  - Update user or employer profile
  - Upload profile images (`POST /api/auth/upload-image`)

- **Job Management**
  - Employers can create, update, and delete jobs
  - Job seekers can view and apply to jobs
  - Save jobs for later

- **Application Management**
  - Job seekers can apply to jobs
  - Employers can view applications for their jobs

- **File Uploads**
  - Secure image upload with backend validation and re-encoding
  - Static file serving via Nginx from `/uploads/`
  - PDF upload and sanitization (if enabled)

- **Database Management**
  - MySQL schema migrations via SQL scripts in `src/main/resources/db/migration/`
  - Custom scripts (e.g., `delete_by_email.sql`) for admin data cleanup

## API Endpoints (Main)

- `POST /api/auth/register` — Register user or employer
- `POST /api/auth/login` — Authenticate and receive JWT
- `POST /api/auth/recover` — Password recovery (send recovery link)
- `POST /api/auth/upload-image` — Upload and validate profile image
- `GET /api/jobs` — List jobs
- `POST /api/jobs` — Create job (employer only)
- `POST /api/jobs/apply` — Apply to job (user only)
- `POST /api/jobs/save` — Save job (user only)

## Static File Serving

- Nginx serves files from `/var/www/html/uploads` 
- Uploaded images are accessible via public URLs returned by the API

## Database Scripts

- Migration scripts: `src/main/resources/db/migration/`
- Admin scripts: `src/main/resources/db/scripts/`
  - Example: `delete_by_email.sql` — Deletes all data for a user or employer by email

## Running the Project

1. Configure MySQL and update `application.properties`.
2. Run migrations (Flyway or manually).
3. Build and start the Spring Boot app:
   ```sh
   mvn package
   java -jar target/recruiting-platform-0.0.1-SNAPSHOT.jar
   ```
4. Ensure Nginx is configured to serve `/uploads/`.

## Security

- File uploads are validated and sanitized
- Only images (PNG, JPG, JPEG, GIF) are allowed for profile uploads
- JWT authentication for protected endpoints

## License

MIT License
