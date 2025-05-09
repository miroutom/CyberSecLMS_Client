FROM golang:1.24 AS builder

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY admin-svc/ ./

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app ./

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /app/app /app/app

EXPOSE 8082

ENTRYPOINT ["/app/app"]
