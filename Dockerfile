FROM eclipse-temurin:17-jdk-jammy as base

WORKDIR /app
# copy maven
COPY .mvn/ .mvn
# copy mvnw and pom file
COPY mvnw pom.xml procedure.sql ./
 
# download dependencies
RUN ./mvnw dependency:resolve
# coppy source
COPY src ./src

# build jar file
# RUN ./mvnw package

# copy to working directory
COPY target/animebe-*.jar /app/animebe.jar

# run app
ENTRYPOINT ["java", "-jar", "/app/animebe.jar"]