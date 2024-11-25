FROM node:18-alpine

WORKDIR src/app

COPY rollup.config.js ./
COPY package*.json ./

RUN npm install

COPY ./src ./src
COPY ./public ./public

RUN npm run build

EXPOSE 57967

ENV HOST=0.0.0.0

CMD ["npm", "start"]