# SonarCloud Metrics Dashboard

A simple Spring Boot application that allows you to export SonarCloud metrics for all your projects to Excel format.

## Features

- **Project Selection**: Choose which projects to include in the export
- **Metric Selection**: Select specific metrics to export
- **Excel Export**: Download metrics data in Excel format
- **Real-time Data**: Fetches live data from SonarCloud API
- **Simple UI**: Clean, responsive web interface

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- SonarCloud account with API token

## Configuration

1. Set your SonarCloud organization and token in `application.yml`:

```yaml
sonarcloud:
  base-url: https://sonarcloud.io/api
  token: YOUR_SONARCLOUD_TOKEN
  organization: YOUR_ORGANIZATION_NAME
```

Or set environment variables:
```bash
export SONARCLOUD_TOKEN=your-token-here
export SONARCLOUD_ORG=your-organization-name
```

## Running the Application

1. Clone the repository
2. Navigate to the backend directory:
   ```bash
   cd backend
   ```

3. Build the application:
   ```bash
   ./mvnw clean install
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Open your browser and go to: `http://localhost:8080`

## Usage

1. **Select Projects**: Choose which projects you want to include in the export
2. **Select Metrics**: Choose which metrics to export (e.g., violations, bugs, code smells, etc.)
3. **Export**: Click "Export to Excel" to download the data
4. **Clear**: Use "Clear Selection" to reset your choices

## API Endpoints

- `GET /` - Main dashboard page
- `GET /api/metrics` - Get all available metrics
- `GET /api/projects` - Get all projects
- `POST /export` - Export selected metrics to Excel

## Technologies Used

- **Backend**: Spring Boot 3.2, WebFlux, Apache POI
- **Frontend**: HTML, CSS, JavaScript, Thymeleaf
- **APIs**: SonarCloud REST API

## Getting Your SonarCloud Token

1. Log in to SonarCloud
2. Go to your account settings
3. Navigate to "Security" tab
4. Generate a new token
5. Copy the token and use it in the configuration

## Troubleshooting

- **No projects found**: Check your organization name and token
- **Export fails**: Ensure you have selected at least one project and metric
- **API errors**: Verify your SonarCloud token has the necessary permissions 