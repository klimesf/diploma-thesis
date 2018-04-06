# Example e-commerce application

## Running the example system with Docker

Make sure you have [Docker](https://www.docker.com/) and [Maven](https://maven.apache.org/)
installed on your system.

First of all, you need to build the docker images. Navigate to the folder and run
the following commands

**Note:** *Please, make sure you have the necessary framework libraries installed and linked.
You will find the instructions in readme files in respective folders for each implementation,
i.e., `java/README.md`, `python/README.md` and `nodejs/README.md`.*

```bash
cd ./billing
mvn clean package dockerfile:build
cd ../shipping
mvn clean package dockerfile:build
cd ../order
mvn clean package dockerfile:build
cd ../ui
mvn clean package dockerfile:build
cd ../product
docker build -t filipklimes-diploma/example-product-service .
cd ../user
docker build -t filipklimes-diploma/example-user-service .
```

Finally, you can run the system via prepared Docker Compose script

```bash
docker-compose up -d
```
