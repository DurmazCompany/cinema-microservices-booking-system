# seat-service

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/seat-service-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t seat-service .
docker run -p 8083:8083 seat-service
```
