FROM python:3.10-alpine3.19

WORKDIR /usr/src/app

EXPOSE 8000

RUN apk add --no-cache openssl && \
    pip install --upgrade pip

COPY requirements.txt /temp/requirements.txt
RUN pip install -r /temp/requirements.txt

COPY . .

RUN chmod +x /usr/src/app/entrypoint.sh

ENTRYPOINT ["/usr/src/app/entrypoint.sh"]



