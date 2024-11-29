import Keycloak from "keycloak-js";

// Configure Keycloak instance
const keycloak = new Keycloak({
  url: "http://localhost:8081",
  realm: "FeedApp",
  clientId: "svelte-client",
});

export default keycloak;
