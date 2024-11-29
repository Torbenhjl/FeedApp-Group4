<script>
  import { onMount } from 'svelte';
  import keycloak from "./keycloak"; // Keycloak initialization
  import Home from './Home.svelte';
  import Polls from './Polls.svelte';
  import CreatePoll from './CreatePoll.svelte';
  import Footer from './Footer.svelte';

  let currentPage = 'home'; // Default page is 'home' after login
  let currentUser = null; // User info from Keycloak
  let authenticated = false;

  // Initialize Keycloak
  const initializeKeycloak = async () => {
      try {
          const auth = await keycloak.init({ onLoad: 'login-required' });
          authenticated = auth;

          if (auth) {
              currentUser = {
                  username: keycloak.tokenParsed?.preferred_username,
              };
              currentPage = 'home'; // Redirect to home after authentication
          }
      } catch (error) {
          console.error('Keycloak initialization failed:', error);
      }
  };

  // Logout function using Keycloak
  const logout = () => {
      keycloak.logout();
      currentUser = null;
      authenticated = false;
      currentPage = 'login';
  };

  // Navigation function
  const navigateTo = (page) => {
      currentPage = page;
  };

  onMount(() => {
      initializeKeycloak();
  });
</script>

<header class="header">
  <div class="logo">
      <img src="/logo.png" alt="Logo">
  </div>

  <nav>
      {#if authenticated}
          <span>Welcome, {currentUser.username}!</span>
          <button on:click={() => navigateTo('home')} class:active={currentPage === 'home'}>Home</button>
          <button on:click={() => navigateTo('polls')} class:active={currentPage === 'polls'}>Polls</button>
          <button on:click={() => navigateTo('createPoll')} class:active={currentPage === 'createPoll'}>Create Poll</button>
          <button on:click={logout}>Logout</button>
      {/if}
  </nav>
</header>

<main>
  {#if currentPage === 'home' && authenticated}
      <Home />
  {:else if currentPage === 'polls' && authenticated}
      <Polls />
  {:else if currentPage === 'createPoll' && authenticated}
      <CreatePoll on:pollCreated={() => navigateTo('polls')} />
  {:else}
      <p>Redirecting to login page...</p>
  {/if}
</main>

<Footer />

<style>
  /* CSS styles for header and navigation */
  .header {
      position: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      background-color: rgba(255, 255, 255, 0.612);
      padding: 2rem 1rem;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      color: #333;
      max-width: 8000px;
      margin: auto;
  }

  .logo img {
      height: 200px;
      margin-bottom: 3rem;
  }

  nav {
      display: flex;
      gap: 2rem;
  }

  span {
      font-weight: bold;
      font-size: 1.1rem;
      color: rgb(3, 3, 3);
      margin-right: auto;
  }

  button {
      background: none;
      border: none;
      color: #555;
      font-size: 1.2rem;
      font-weight: 500;
      cursor: pointer;
      position: relative;
      padding: 0.5rem 0;
      transition: color 0.3s ease;
  }

  button.active {
      color: #b00;
      font-weight: bold;
  }

  button.active::after {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      bottom: -2px;
      height: 2px;
      background-color: #b00;
  }

  button:hover {
      color: #333;
  }
</style>
