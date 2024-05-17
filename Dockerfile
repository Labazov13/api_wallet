FROM openjdk:17
LABEL authors="labazov"
WORKDIR /app
COPY target/api_wallet-0.0.1-SNAPSHOT.jar api_wallet.jar
EXPOSE 8080
CMD ["java", "-jar", "api_wallet.jar"]