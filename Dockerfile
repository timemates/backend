# Use a base image with Java and PostgreSQL
FROM openjdk:17-oracle

# Set the working directory inside the container
WORKDIR /app

ENV DOCKER_IMAGE_PORT = 8080

# Install wget utility for downloading files
RUN apt-get update && apt-get install -y wget && apt-get install -y default-jre

# Set the desired version of your backend JAR file
ARG JAR_VERSION=v1.0.0-M2

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
