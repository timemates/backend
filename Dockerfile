# Stage 1: Download JAR file from URL
FROM ubuntu AS DownloadStage

ARG APPLICATION_VERSION

# Download JAR file from the specified URL
ARG JAR_DOWNLOAD_URL=https://github.com/timemates/backend/releases/download/$APPLICATION_VERSION/application.jar
RUN wget --quiet --show-progress --no-cache --progress=bar: ${JAR_DOWNLOAD_URL} -O /downloaded.jar

# Stage 2: Build the final image
FROM amazoncorretto:19 AS RunStage

# Copy the downloaded JAR file from the previous stage
COPY --from=DownloadStage /downloaded.jar /app/application.jar

# Set environment variables and expose ports
ENV DOCKER_IMAGE_PORT=8080
ENV TIMEMATES_GRPC_PORT=8181
ENV TIMEMATES_RSOCKET_PORT=8282
EXPOSE $TIMEMATES_GRPC_PORT
EXPOSE $TIMEMATES_RSOCKET_PORT

# Run the application
CMD ["java", "-jar", "/app/application.jar"]
