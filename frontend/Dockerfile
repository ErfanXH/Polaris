FROM node:22.16.0-slim

WORKDIR /front

COPY package.json package-lock.json ./

RUN npm install

COPY . .