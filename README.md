# TDC PoA 2025

This project contains a complete authentication and authorization stack using Keycloak, Envoy Proxy, Open Policy Agent (OPA), and a Spring Boot application.

## Prerequisites

- Java 25 (or compatible version)
- Docker and Docker Compose
- OpenSSL (for certificate generation)
- Gradle (via wrapper)

## Quick Start

### 1. Generate SSL Certificates

First, generate the SSL certificates required for the nginx proxy:

```bash
./gradlew generateSslCertificates
```

This will create:
- `docker/nginx/etc/ssl/certs/localhost.crt`
- `docker/nginx/etc/ssl/private/localhost.key`

### 2. Start the Application Stack

Build and start all services using Docker Compose:

```bash
docker compose up --build
```

This will start:
- **PostgreSQL** - Database for Keycloak
- **Keycloak** - Authentication server
- **Nginx** - Reverse proxy with SSL
- **Envoy** - Service proxy with ext_authz
- **OPA** - Policy engine for authorization
- **Spring Boot App** - Sample application

### 3. Access the Application

Once all services are running, to experiment with the example:
- Open the front-end URL
- Click on login, and enter the credentials
- You will see a list of items, try to add a new one, and delete one.
- At first time, Keycloak will ask you to set a 2FA and a passkey, depending on the flow you chose, after the setup log off the application and execute the flows again, now with an account fully configured.

**URL:** https://localhost/front

**Credentials:**
- Username: `testuser`
- Password: `password`



Access Keycloak at:
**URL:** https://localhost/auth

**Credentials:**
- Username: `admin`
- Password: `adminpassword`

### 4. Access Other Services

- **Keycloak Admin Console:** https://localhost/auth
- **Spring Boot API:** https://localhost/api/items
- **Envoy Admin Interface:** http://localhost:9901

## Project Structure

```
.
├── docker/
│   ├── app/                    # Spring Boot application Dockerfile
│   ├── auth-server/            # Keycloak custom Dockerfile (optional)
│   ├── envoy/                  # Envoy proxy configuration
│   ├── nginx/                  # Nginx configuration and SSL certificates
│   └── opa/                    # OPA policy files
├── config/
│   └── import/                 # Keycloak realm import files
├── src/                        # Spring Boot application source code
└── docker-compose.yaml         # Docker Compose configuration
```

## Development

### Building the Spring Boot Application

```bash
./gradlew bootJar
```

### Running Tests

```bash
./gradlew test
```

### Viewing Logs

View logs for all services:
```bash
docker compose logs -f
```

View logs for a specific service:
```bash
docker compose logs -f keycloak
docker compose logs -f app
docker compose logs -f envoy
docker compose logs -f opa
```

## Cleanup

### ⚠️ Warning: Docker System Prune

**IMPORTANT:** The following command will remove **ALL** unused Docker resources (containers, networks, images, and build cache) from your system. This can affect other Docker projects you may have running.

```bash
docker system prune -a --volumes
```

**Use this only if:**
- You don't have other Docker projects running
- You want to completely clean up your Docker environment
- You understand this will remove all unused Docker resources

### Manual Cleanup (Recommended for Multi-Project Environments)

If you have other Docker projects running, use these targeted cleanup commands:

#### 1. Stop and Remove Containers

```bash
docker compose down
```

This stops and removes containers, networks, and volumes defined in this project's `docker-compose.yaml`.

#### 2. Remove Project-Specific Volumes

```bash
docker volume rm tdc-poa-2025_pgdata
```

Or list all volumes first to identify project-specific ones:
```bash
docker volume ls
```

#### 3. Remove Project Images (Optional)

```bash
docker images | grep tdc-poa-2025
docker rmi <image-id>
```

Or remove images built by this project:
```bash
docker images --filter "reference=tdc-poa-2025*" -q | xargs docker rmi
```

#### 4. Remove Project Networks

```bash
docker network ls | grep tdc-poa-2025
docker network rm tdc-poa-2025_auth
```

#### 5. Clean Build Artifacts (Optional)

Remove Gradle build artifacts:
```bash
./gradlew clean
```

Remove generated SSL certificates:
```bash
./gradlew cleanSslCertificates
```

### Complete Manual Cleanup Checklist

For a thorough cleanup without affecting other Docker projects:

```bash
# 1. Stop and remove containers
docker compose down

# 2. Remove volumes
docker volume rm tdc-poa-2025_pgdata

# 3. Remove networks
docker network rm tdc-poa-2025_auth

# 4. Remove images (if needed)
docker images --filter "reference=tdc-poa-2025*" -q | xargs docker rmi

# 5. Clean Gradle build
./gradlew clean

# 6. Remove SSL certificates (optional)
./gradlew cleanSslCertificates
```

## Troubleshooting

### Port Conflicts

If you encounter port conflicts, check what's using the ports:

```bash
# Check port 8080 (Keycloak)
lsof -i :8080

# Check port 443 (Nginx)
lsof -i :443

# Check port 8081 (Spring Boot App)
lsof -i :8081
```

### SSL Certificate Issues

If you see SSL errors, regenerate the certificates:

```bash
./gradlew regenerateSslCertificates
```

### Keycloak Not Starting

Check Keycloak logs:
```bash
docker compose logs keycloak
```

Ensure PostgreSQL is healthy:
```bash
docker compose ps postgres
```

## Exporting keycloak realm
```shell
docker exec keycloak-server /opt/keycloak/bin/kc.sh export --dir /tmp/keycloak-export --realm sample-tdc --users different_files
```
```shell
docker cp keycloak-server:/tmp/keycloak-export/sample-tdc-realm.json ./config/import; 
docker cp keycloak-server:/tmp/keycloak-export/sample-tdc-users-0.json ./config/import; 
```

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Envoy Proxy Documentation](https://www.envoyproxy.io/docs)
- [Open Policy Agent Documentation](https://www.openpolicyagent.org/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
