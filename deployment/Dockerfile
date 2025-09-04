FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
#COPY *.jar requests.jar
COPY applications/app-service/build/libs/requests.jar requests.jar
#ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Replace with a non-root loanRequest to avoid running the container with excessive privileges
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar requests.jar" ]
EXPOSE 8081