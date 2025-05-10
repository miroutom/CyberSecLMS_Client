# Как добавить новый микросервис (на Go или Python)

## 1. Создай сервис-каталог внутри репозитория

Например: ./my-new-service/

Главный entrypoint должен быть внутри — например, `main.go` для Go или `app.py` для Python.

---

## 2. Dockerfile для Go-микросервиса

```dockerfile
FROM golang:1.24 AS builder

WORKDIR /go/src/lmsmodule

COPY . .

RUN go mod download
RUN CGO_ENABLED=0 GOOS=linux go build -buildvcs=false -a -installsuffix cgo -o /go/bin/my-new-service ./my-new-service

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /go/bin/my-new-service /app/my-new-service

EXPOSE 8082  # Порт, который слушает сервис (замени на нужный!)

ENTRYPOINT ["/app/my-new-service"]
```

Пояснения:
- Первый слой соберёт бинарник Go.
- Второй слой — минимальный образ, только бинарник.
- EXPOSE — порт, который слушает сервис.
- ENTRYPOINT — старт сервиса внутри контейнера.

## 3. Dockerfile для Python-микросервиса
```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY ./my-python-service/ /app/

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

EXPOSE 8090  # Порт, который слушает сервис

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8090"]
```

main:app — путь к точке входа приложения (например, для FastAPI — функция app в main.py).
Если у тебя Flask или Django, замени команду на подходящую (например, gunicorn для WSGI).

## 4. Добавь сервис в docker-compose.yml
```yaml
my-new-service:
image: ${DOCKER_HUB_USERNAME}/my-new-service:latest
build:
context: .
dockerfile: ./my-new-service/Dockerfile
ports:
- "8082:8082"             # Откроет порт наружу, если нужно
environment:
# Здесь переменные окружения: ключи, DSN к базе и прочее
depends_on:
# что нужно (например, db, redis, discovery-server)
networks:
- cybersec
restart: always
```

Не забудь добавить сеть (обычно cybersec).
Если сервис нужен в CI/CD, обнови .env и pipeline для build/push.

## 5. Где брать шаблоны и примеры
- Go — смотри существующие сервисы типа api-gateway, backend-svc в этом репозитории. 
- Python — смотри FastAPI Docker guide или Flask Docker.