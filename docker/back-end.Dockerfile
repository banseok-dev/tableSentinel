FROM gradle:jdk21-jammy AS builder
WORKDIR /home/gradle/project/back-end

# INFO: Context pointer [/tableSentinel] [/WORKDIR]
COPY --chown=gradle:gradle ./back-end .
COPY --chown=gradle:gradle ./proto ../proto

# INFO : /home/gradle/project/back-end
RUN ./gradlew generateProto
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /home/gradle/project/back-end/build/libs/*.jar tableSentinel_backend.jar

# Port mapping (8080:tomcat, 8081:netty)
EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "tableSentinel_backend.jar"]
