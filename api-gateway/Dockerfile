FROM golang:1.24 AS builder

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY api-gateway/ ./

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app ./

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /app/app /app/app
COPY --from=builder /app/configs /app/configs

EXPOSE 8080

ENTRYPOINT ["/app/app"]
