FROM quay.io/keycloak/keycloak:latest AS builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=postgres

WORKDIR /opt/keycloak

# No need to generate SSL keys for HTTP
# RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Change these values to point to a running postgres instance
ENV KC_DB=postgres
ENV KC_DB_URL=jdbc:postgresql://postgres:5432/petstore
ENV KC_DB_USERNAME=pet
ENV KC_DB_PASSWORD=meow
ENV KC_HOSTNAME=localhost

# Disable SSL
ENV KC_HTTP_ENABLED=true
ENV KC_HOSTNAME_STRICT=false
ENV KC_HOSTNAME_STRICT_HTTPS=false

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
