FROM node:18 AS builder

WORKDIR /app
COPY ./frontend-svc/ .

RUN npm install && npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/conf.d/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
