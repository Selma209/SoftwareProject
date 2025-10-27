# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:19-jre
WORKDIR /app
COPY --from=build /app/target/bankease-1.0.0.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
