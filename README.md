# Film Rental Store - Full Stack Java Project

A comprehensive Film Rental Store management system built with Spring Boot, MySQL, and Thymeleaf.

## 📁 Project Structure

- `backend/`: Spring Boot REST API
  - Handles business logic, database entities, and repositories.
  - Standardized API endpoints with `/api` prefix.
  - Swagger UI: `http://localhost:8082/swagger-ui/index.html`
- `frontend/`: Spring Boot + Thymeleaf UI
  - Consumes the backend REST APIs.
  - Runs on port `8081`.
- `db/`: Database scripts
  - `film_rental_full.sql`: Full schema and data for the `film_rental` database.

## 🚀 How to Run Locally

### 1. Database Setup
1. Open your MySQL terminal or Workbench.
2. Run the script found in `db/film_rental_full.sql`.
   ```sql
   source path/to/db/film_rental_full.sql;
   ```
3. Ensure the database `film_rental` is created and populated.

### 2. Backend Configuration
1. Open `backend/src/main/resources/application.properties`.
2. Update your MySQL credentials:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

### 3. Run Backend
1. Navigate to the `backend` directory.
2. Run using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Backend will be available at `http://localhost:8082`.

### 4. Run Frontend
1. Navigate to the `frontend` directory.
2. Run using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Frontend will be available at `http://localhost:8081`.

## 🌐 Deployment

### Backend (Render / Railway)
- **Runtime**: Java 17+
- **Build Command**: `./mvnw clean install -DskipTests`
- **Start Command**: `java -jar target/Film_Rental_System-0.0.1-SNAPSHOT.jar`
- **Environment Variables**:
  - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<your-db-host>:3306/film_rental`
  - `SPRING_DATASOURCE_USERNAME`: `<your-db-user>`
  - `SPRING_DATASOURCE_PASSWORD`: `<your-db-password>`

### Frontend (Render / Railway)
- **Runtime**: Java 17+
- **Build Command**: `./mvnw clean install -DskipTests`
- **Start Command**: `java -jar target/frontend-0.0.1-SNAPSHOT.jar`

## ✅ Key Features Fixed
- **Standardized API paths**: All backend controllers now use the `/api` prefix.
- **CORS Enabled**: Backend now accepts requests from the frontend origin.
- **Database Alignment**: Configuration updated to use the `film_rental` database.
- **Dependency Fixes**: Cleanup of unnecessary files and standardized project structure.
