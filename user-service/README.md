# user-service

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t user-service .
docker run -p 8081:8081 user-service
```
