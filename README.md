# 🛠 BOT CREATOR APP - RUNNING LOCALLY VIA DOCKER

This guide explains how to **clone, build, and run** the Bot Creator application inside a Docker container using Docker Compose.

---

## 🔧 Requirements

Before you begin, make sure you have the following installed:

- [Docker](https://www.docker.com/products/docker-desktop) (version 20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Git](https://git-scm.com/)

---

## 📄 Notes

- The application uses:
    - Java 21
    - Spring Boot 3.5
    - Gradle 8.5
- Profile-specific config like `application-111.yml` must exist under `src/main/resources/`
- Swagger/OpenAPI docs (if enabled) will be available at:
  ```
  http://localhost:8080/swagger-ui/index.html
  ```

---

## 📦 Step 1: Clone the repository

```bash
git clone <your-repo-url>
cd <your-project-folder>
```

Replace `<your-repo-url>` and `<your-project-folder>` with your actual Git repository link and project folder name.

---

## ⚙️ Step 2: Build the Docker image

Make sure the project root contains the following files:

- `Dockerfile`
- `docker-compose.yml`

To build the Docker image:

```bash
docker-compose build
```

This command will:
- Use a Gradle container (JDK 21) to build the Spring Boot application
- Package the app into a JAR file
- Copy the JAR into a minimal runtime image (Java 21 JRE)

---

## 🚀 Step 3: Run the application

To run the application using a specific Spring profile (e.g. `111`), execute:

```bash
docker-compose up -d
```

This starts the app in detached mode and exposes:

```
http://localhost:8080
```

To view logs:

```bash
docker logs -f bot-creator
```

---

## 🌱 Switching Spring Profiles

The active Spring profile is defined in `docker-compose.yml`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: 111
```

To change it:

1. Edit the value to your desired profile
2. Re-run the container:

```bash
docker-compose down
docker-compose up -d
```

No need to rebuild unless application logic has changed.

---

## 🧪 API Testing via Swagger UI

After starting the app, you can test the bot creation API visually using Swagger UI:

### ✅ Open:
```
http://localhost:8080/swagger-ui/index.html
```

### ✅ Sample Request

Use the `/api/bots` endpoint with a payload like:

```json
{
  "startIndex": 12, 
  "endIndex": 14,
  "botNamePrefix": "test-bot-username",
  "botPassword": "123123"
}
```
