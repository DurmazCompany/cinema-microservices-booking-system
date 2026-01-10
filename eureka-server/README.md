# eureka-server

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t eureka-server .
docker run -p 8761:8761 eureka-server
```
