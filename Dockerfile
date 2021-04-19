FROM openjdk:14-alpine AS run
COPY target /home/app/target
RUN cp /home/app/target/spring-boot-s3-client-*.jar /home/app/app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=dev", "-jar", "/home/app/app.jar"]
