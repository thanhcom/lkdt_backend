# ==============================
# Stage 1: Build
# ==============================
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar, skip tests để nhanh
RUN mvn clean package -DskipTests

# ==============================
# Stage 2: Run (JRE nhẹ)
# ==============================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port Spring Boot
EXPOSE 8084

# ENTRYPOINT
ENTRYPOINT ["java","-jar","app.jar"]
