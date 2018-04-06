# Node.js library for the business context framework

## Building Docker image

First, make sure you have [Docker](https://www.docker.com/) installed on your computer.
Navigate to the **project root folder** and run the following command. (this is because we need to link files from `proto/` folder)

```bash
docker build -f nodejs/Dockerfile -t filipklimes-diploma/nodejs .
```

You can now use and extend `filipklimes-diploma/nodejs` Docker image.
