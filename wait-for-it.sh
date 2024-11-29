#!/bin/bash
set -e

echo "Waiting for Keycloak to be healthy..."
while ! curl -s http://keycloak:8081/realms/master > /dev/null; do
    echo "Keycloak not ready yet..."
    sleep 5
done

echo "Keycloak is ready. Starting Spring Boot app."
exec java -jar /app.jar