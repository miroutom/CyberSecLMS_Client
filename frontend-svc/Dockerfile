FROM node:18 AS build

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

FROM nginx:stable-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/nginx.conf
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ./nginx/certs /etc/nginx/certs

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
