FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/libraryhub.jar app.jar
ENV JAVA_OPTS="-Xmx400m"
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]