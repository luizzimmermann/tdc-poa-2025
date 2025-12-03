package istio.authz

import rego.v1

default allow := {"allowed": true}

allow := {
    "allowed": false,
    "http_status": 401,
    "headers": {
        "WWW-Authenticate": "Bearer error=\"insufficient_user_authentication\", error_description=\"A different authentication level is required\", acr_values=\"2\""
    }
} if {
    input.attributes.request.http.path == "/api/items"
    input.attributes.request.http.method == "POST"
    [_, claims, _] := io.jwt.decode(bearer_token)
    claims.acr
    acr := claims.acr
    to_number(acr) < 2
}

allow := {
    "allowed": false,
    "http_status": 401,
    "headers": {
        "WWW-Authenticate": "Bearer error=\"insufficient_user_authentication\", error_description=\"A different authentication level is required\", acr_values=\"3\""
    }
} if {
    startswith(input.attributes.request.http.path, "/api/items")
    input.attributes.request.http.method == "DELETE"
    [_, claims, _] := io.jwt.decode(bearer_token)
    claims.acr
    acr := claims.acr
    to_number(acr) < 3
}

bearer_token := t if {
    auth_header := input.attributes.request.http.headers.authorization
    startswith(auth_header, "Bearer ")
    t := substring(auth_header, 7, -1)
}