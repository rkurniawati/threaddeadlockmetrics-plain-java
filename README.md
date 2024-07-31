# Plain Java application with Thread Deadlock Metrics

This is an example Spring Boot application that demonstrates how to expose thread deadlock metrics using Micrometer. More information can be found in this [article](https://medium.com/@ruth.kurniawati/detecting-deadlock-with-micrometer-metrics-a8b71ad63cb3).

To run the application, first use `gradlew` to build the application:

```bash
./gradlew jar
```

Then, run the application using the following command:

```bash
java -jar build/libs/plain-java-1.0-SNAPSHOT.jar
```
