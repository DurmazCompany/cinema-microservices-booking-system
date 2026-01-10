# catalog-service

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/catalog-service-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t catalog-service .
docker run -p 8082:8082 catalog-service
```
