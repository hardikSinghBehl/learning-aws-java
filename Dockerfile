FROM maven:3.9.9-amazoncorretto-23 as backend
WORKDIR /backend
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:23
ARG DEPENDENCY=/backend/target/dependency
COPY --from=backend ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=backend ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=backend ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.demo.Application"]
