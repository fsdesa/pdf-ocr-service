FROM openjdk:8-alpine

RUN apk update
RUN apk add bash
RUN apk add graphicsmagick

ENV APP_FILE pdf-ocr-service-0.0.1.jar
EXPOSE 8081
COPY target/$APP_FILE /app.jar

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
