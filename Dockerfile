FROM eclipse-temurin:17-jre-alpine@sha256:839f3208bfc22f17bf57391d5c91d51c627d032d6900a0475228b94e48a8f9b3
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
