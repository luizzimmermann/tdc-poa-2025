# Keycloak Realm Import Directory

Place your Keycloak realm JSON export files in this directory. Keycloak will automatically import and **overwrite** them at startup.

## Usage

1. Export your realm from Keycloak Admin Console (Realm Settings → Export)
2. Place the exported JSON file(s) in this directory
3. Start Keycloak with `docker-compose up`

Keycloak will import all `.json` files found in this directory during startup using the `--override` flag, which means **existing realms will be replaced** with the imported configuration.

## File Naming

- Realm files should be named with the realm name (e.g., `myrealm.json`)
- Multiple realms can be imported by placing multiple JSON files

## Notes

- The import happens automatically on every startup
- **Existing realms will be overwritten** - all configurations (users, roles, clients, etc.) will be replaced
- Make sure to backup important data before restarting if you don't want to lose changes made through the Admin Console

