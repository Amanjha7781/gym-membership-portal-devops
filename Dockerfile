# CI and local workflows compile/test with Maven first; this image packages the verified JAR.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S gym && adduser -S gym -G gym
COPY target/gym-membership-portal-1.0.0.jar app.jar
USER gym
EXPOSE 8080
HEALTHCHECK --interval=20s --timeout=3s --start-period=45s --retries=3 CMD wget -q -O /dev/null http://127.0.0.1:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
