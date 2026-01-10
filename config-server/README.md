# config-server

## Build
```bash
mvn clean package
```

## Run
```bash
java -jar target/config-server-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t config-server .
docker run -p 8888:8888 config-server
```
