# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/ku-ku-app-0.0.1-SNAPSHOT.jar app.jar

# Render dynamic port
ENV PORT=10000

EXPOSE 10000

CMD sh -c "java -Dserver.port=$PORT -Dserver.address=0.0.0.0 -jar app.jar"