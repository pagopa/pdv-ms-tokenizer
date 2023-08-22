FROM adoptopenjdk/openjdk11:alpine-jre@sha256:789f551fca5e233b1593aee65283d5981b3a82284e15a8cad4c798315188f848
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
