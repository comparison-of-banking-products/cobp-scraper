FROM mcr.microsoft.com/playwright/java:v1.40.0-jammy
COPY ["target/*.jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]
