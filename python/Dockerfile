FROM python:3

WORKDIR /usr/src/framework

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt && cd ..
COPY . .
RUN pip install -e .
