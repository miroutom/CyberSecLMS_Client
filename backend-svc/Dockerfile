FROM golang:1.24 AS builder

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY backend-svc/ ./

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app ./

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /app/app /app/app

EXPOSE 8081

ENTRYPOINT ["/app/app"]
