# booking-service

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/booking-service-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t booking-service .
docker run -p 8084:8084 booking-service
```
