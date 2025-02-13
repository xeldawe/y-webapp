# Stage 1: Build the Angular Universal application
FROM node:22.13.1 AS build

# Set the working directory
WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install the dependencies
RUN npm install --legacy-peer-deps

# Install Angular CLI globally and set permissions
RUN npm install -g @angular/cli && chmod +x /usr/local/bin/ng

# Copy the rest of the application source code
COPY . .

# Set the environment variable during the build
ARG API_KEY
ENV API_KEY=${API_KEY}
ARG FILTER_INTERVAL
ENV FILTER_INTERVAL=${FILTER_INTERVAL}
ARG API_URL
ENV API_URL=${API_URL}

# Run the set-env.js script
RUN node set-env.js

# Build the Angular application
RUN npm run build --prod

# Stage 2: Serve the Angular Universal application
FROM node:22.13.1

# Set the working directory
WORKDIR /app

# Copy the built Angular Universal application from Stage 1
COPY --from=build /app/dist /app/dist

# Install PM2 globally
RUN npm install pm2 -g

# Set the environment variable for the runtime
ENV API_KEY=${API_KEY}
ENV FILTER_INTERVAL=${FILTER_INTERVAL}
ENV API_URL=${API_URL}

# Expose port 4000 (or your chosen port)
EXPOSE 4000

# Start the SSR server using PM2 with 3 replicas
CMD ["pm2-runtime", "start", "dist/frontend/server/server.mjs", "--instances", "3"]
