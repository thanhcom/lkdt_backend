# ==============================
# Stage 1: Build
# ==============================
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước để cache layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code
COPY src ./src

# Build jar (skip test để nhanh)
RUN mvn clean package -DskipTests

# ==============================
# Stage 2: Run (tối ưu, dùng JRE)
# ==============================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar ./app.jar

# Expose port cấu hình Spring Boot
EXPOSE 8084

# Cấu hình environment variables
ENV JWT_SIGNER_KEY=""
ENV JWT_SIGNER_KEY_REFRESH=""
ENV JWT_TOKEN_TIME=300
ENV JWT_REFRESH_TOKEN_TIME=30
ENV DB_URL="jdbc:postgresql://100.114.136.16:5433/postgres"
ENV DB_USERNAME="supabase_admin"
ENV DB_PASSWORD="laodaicaha"

# Chạy Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
