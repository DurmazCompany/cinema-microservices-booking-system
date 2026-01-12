FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY config-server/pom.xml ./pom.xml
COPY config-server/src ./src
COPY config-repo ./config-repo
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/config-repo ./config-repo
ENTRYPOINT ["java","-jar","/app.jar"]
