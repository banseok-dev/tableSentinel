FROM node:25.2.1-alpine AS builder
WORKDIR /app

# INFO: Context pointer [/tableSentinel] [/WORKDIR]
COPY ./front-end/package*.json ./
RUN npm install

COPY ./front-end .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# /tableSentinel                      docker container
COPY ./docker/nginx-conf/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
