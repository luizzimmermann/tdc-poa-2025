###########
# Builder #
###########
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files first for better layer caching
COPY keycloak-angular/package*.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the source code
COPY keycloak-angular/ ./

# Build the keycloak-angular library first
RUN npm run build

# Build the standalone example with base-href for /front path
RUN npx ng build example-standalone --configuration=production --base-href=/front/

############
# Runtime  #
############
FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Copy built Angular app from builder stage
COPY --from=builder /app/dist/examples/standalone/browser /usr/share/nginx/html/front

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

