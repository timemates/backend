# Use a base image with Java and PostgreSQL
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

ENV DOCKER_IMAGE_PORT = 8080

# Set the desired version of your backend JAR file
ARG JAR_VERSION=v1.0.0-M5

# Set the download URL for the JAR file
ARG JAR_DOWNLOAD_URL=https://github.com/timemates/backend/releases/download/${JAR_VERSION}/application.jar || true

# Download the JAR file from the specified URL
RUN wget --quiet --show-progress --no-cache --progress=bar: ${JAR_DOWNLOAD_URL} -O application.jar

# Expose the port on which your application will run
EXPOSE $TIMEMATES_GRPC_PORT
EXPOSE $TIMEMATES_RSOCKET_PORT

# Set the command to run application
# Refer to the documentation what environment variables should be set to run application
CMD ["java", "-jar", "application.jar"]
