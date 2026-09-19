FROM node:20-alpine AS build
WORKDIR /app

# Copy dependency manifests first
COPY package*.json ./

# Install dependencies cleanly inside the Alpine Linux environment
RUN npm install

# Copy source code (use .dockerignore to exclude local node_modules!)
COPY . .

EXPOSE 4200
CMD ["npm", "run", "start:docker"]
