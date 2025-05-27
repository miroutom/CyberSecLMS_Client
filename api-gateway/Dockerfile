FROM golang:1.24 AS builder

WORKDIR /go/src/lmsmodule

COPY . .

RUN go mod download
RUN CGO_ENABLED=0 GOOS=linux go build -buildvcs=false -a -installsuffix cgo -o /go/bin/api-gateway ./api-gateway

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /go/bin/api-gateway /app/api-gateway/api-gateway
COPY --from=builder /go/src/lmsmodule/api-gateway/configs /app/api-gateway/configs

EXPOSE 8080

ENTRYPOINT ["/app/api-gateway/api-gateway"]
