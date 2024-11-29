<script>
  import keycloak from "./keycloak";

  let authenticated = false;

  // Initialize Keycloak with the login-required setting to force authentication
  keycloak
    .init({ onLoad: "login-required" })
    .then((auth) => {
      authenticated = auth;
      if (auth) {
        console.log("Authenticated");
      } else {
        console.log("Not Authenticated");
      }
    })
    .catch((error) => {
      console.error("Keycloak initialization failed:", error);
    });

  // Logout function to trigger Keycloak's logout
  const logout = () => {
    keycloak.logout();
  };

  // Fetch the token if authenticated
  const getToken = async () => {
    if (keycloak.token) {
      await keycloak.updateToken(10); // Refresh token if necessary
      return keycloak.token;
    }
    return null;
  };
</script>

{#if authenticated}
  <h1>Welcome, {keycloak.tokenParsed?.preferred_username}!</h1>
  <button on:click={logout}>Logout</button>
{:else}
  <!-- Keycloak will automatically redirect to its login page if not authenticated -->
  <p>Redirecting to login page...</p>
{/if}
