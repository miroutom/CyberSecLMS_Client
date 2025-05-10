FROM node:18 AS build

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

FROM nginx:stable-alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ./nginx/certs /etc/nginx/certs

EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
