FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json /app/
RUN npm i
COPY . /app
EXPOSE 4200
CMD ["npm", "start"]
