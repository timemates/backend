FROM amazoncorretto:19

# Build backend jar from `:app` module
CMD ["./gradlew", ":app:shadowJar"]

# Define ENVs that we will use
ENV DOCKER_IMAGE_PORT=8080
ENV TIMEMATES_GRPC_PORT=8181
ENV TIMEMATES_RSOCKET_PORT=8282

# Expose the port on which your application will run
EXPOSE $TIMEMATES_GRPC_PORT
EXPOSE $TIMEMATES_RSOCKET_PORT

# Set the command to run application
# Refer to the documentation what environment variables should be set to run application
CMD ["java", "-jar", "app/build/libs/application.jar"]
