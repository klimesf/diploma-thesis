# Central administration service for the business context framework

## Running the service with Docker

Make sure you have [Docker](https://www.docker.com/) and [Maven](https://maven.apache.org/)
installed on your system.

First of all, you need to install the java library. Navigate to the folder
and run the following commands

```bash
cd ../java
mvn clean install
cd ../central-administration
mvn clean package dockerfile:build
```

You can now run the central administration

```
docker run --rm -it filipklimes-diploma/central-administration
```
