# Use a base image with Java and PostgreSQL
FROM openjdk:11-alpine-jdk
FROM postgres:15.3

# Set the working directory inside the container
WORKDIR /app

ENV DOCKER_IMAGE_PORT = 8080

# Install wget utility for downloading files
RUN apt-get update && apt-get install -y wget

# Set the desired version of your backend JAR file
ARG JAR_VERSION=1.0.0-M1

# Set the download URL for the JAR file
ARG JAR_DOWNLOAD_URL=https://github.com/timemates/backend/releases/download/${JAR_VERSION}/application.jar

# Download the JAR file from the specified URL
RUN wget --quiet --show-progress --progress=bar: ${JAR_DOWNLOAD_URL} -O application.jar || true

# Expose the port on which your application will run
EXPOSE $SERVER_PORT

# Set the command to run application
# Refer to the documentation what environment variables should be set to run application
CMD ["java", "-jar", "application.jar"]
