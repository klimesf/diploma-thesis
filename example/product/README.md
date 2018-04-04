# Product service

## Building & running

First you have to build the Python framework

```bash
cd ../../python
pip3 install -e .
```


Then, install requirements with `pip`

```bash
pip3 install -r requirements.txt
```

And run the service

```bash
python3 product_service.py
```

The service should welcome you with the following message:

```
BusinessContextServer listening on port 5552
 * Running on http://127.0.0.1:5502/ (Press CTRL+C to quit)
```
