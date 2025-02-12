FROM node:22.13.1

WORKDIR /usr/src/app/api

COPY api/package*.json ./

# Clean npm cache and update npm
RUN npm cache clean --force \
    && npm install -g npm@latest \
    && npm install

COPY api .

EXPOSE 3000

# Start the app
CMD ["node", "src/app.js"]
