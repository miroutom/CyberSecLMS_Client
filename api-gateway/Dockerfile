FROM golang:1.24 AS builder

WORKDIR /app

COPY . .

RUN go mod download
RUN CGO_ENABLED=0 GOOS=linux go build -buildvcs=false -a -installsuffix cgo -o api-gateway ./api-gateway

FROM gcr.io/distroless/base-debian12

WORKDIR /app

COPY --from=builder /app/api-gateway /app/
COPY --from=builder /app/api-gateway/configs /app/configs/

EXPOSE 8080

ENTRYPOINT ["./api-gateway", "--config", "./configs/config.yaml"]
