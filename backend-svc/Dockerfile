FROM golang:1.24 AS builder

WORKDIR /go/src/lmsmodule

COPY . .

RUN go mod download
RUN CGO_ENABLED=0 GOOS=linux go build -buildvcs=false -a -installsuffix cgo -o /go/bin/backend-svc ./backend-svc

FROM gcr.io/distroless/base-debian12

WORKDIR /app
COPY --from=builder /go/bin/backend-svc /app/backend-svc

EXPOSE 8081

ENTRYPOINT ["/app/backend-svc"]
