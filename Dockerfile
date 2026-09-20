FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package dependency:copy-dependencies \
    -DoutputDirectory=target/dependency


FROM eclipse-temurin:25-jre-ubi10-minimal

WORKDIR /app

COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/dependency ./dependency

COPY data ./data

RUN mkdir -p output

CMD ["java", "-cp", "classes:dependency/*", "com.manolo.Main"]