FROM quay.io/keycloak/keycloak:26.4.6 AS builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=postgres

WORKDIR /opt/keycloak
# Build Keycloak with the configured options
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.4.6
COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Database configuration (will be overridden by docker-compose if needed)
ENV KC_DB=postgres
ENV KC_DB_URL=jdbc:postgresql://postgres:5432/keycloak

# Proxy and HTTP configuration (KC_PROXY is deprecated)
ENV KC_PROXY_HEADERS=xforwarded
ENV KC_HTTP_ENABLED=true

# Hostname configuration for development
ENV KC_HOSTNAME_STRICT=false
ENV KC_HOSTNAME_STRICT_HTTPS=false

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]