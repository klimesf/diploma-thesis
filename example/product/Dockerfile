FROM filipklimes-diploma/python

WORKDIR /usr/src/app
COPY requirements.txt ./
RUN pip3 install --no-cache-dir -r requirements.txt
COPY . .

EXPOSE 5502
EXPOSE 5552

ENTRYPOINT ["python3", "product_service.py"]
