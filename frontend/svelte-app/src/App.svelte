<script>
    import { onMount } from 'svelte'; 
    import Login from './Login.svelte';
    import Register from './Register.svelte';
    import Home from './Home.svelte';
    import Polls from './Polls.svelte';
    import CreatePoll from './CreatePoll.svelte';
    import Footer from './Footer.svelte';
    

    let currentPage = 'login';  // Default page is 'login'
    let currentUser = null;     // Logged-in user
    let error = '';

    // Function to fetch the current user
    const getCurrentUser = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/users/current', {
                credentials: 'include',
            });
            if (res.ok) {
                currentUser = await res.json();
                if (currentUser) {
                    currentPage = 'home';  // Redirect to 'home' if user is logged in
                }
            } else {
                currentUser = null;
            }
        } catch (err) {
            console.error('Error fetching user:', err);
            error = 'Could not fetch the logged-in user.';
        }
    };

    // Function to handle logout
    const logout = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/users/logout', {
                method: 'POST',
                credentials: 'include'
            });
            if (res.ok) {
                currentUser = null;  // Clear the currentUser
                currentPage = 'login';  // Redirect to login
            } else {
                alert('Failed to log out');
            }
        } catch (error) {
            console.error('Error logging out:', error);
            alert('An error occurred while logging out');
        }
    };

    // Navigation function
    function navigateTo(page) {
        currentPage = page;
    }

    // Fetch the current user when the app loads
    onMount(() => {
        getCurrentUser();
    });



</script>

<header class="header">
    <!-- Logo centered at the top -->
    <div class="logo">
        <img src="/logo.png" alt="Logo">
    </div>

    <!-- Navigation buttons centered below the logo -->
    <nav>
        {#if currentUser}
            <span>Logged in as: {currentUser.username}</span>
            <button on:click={() => navigateTo('home')} class:active={currentPage === 'home'}>Home</button>
            <button on:click={() => navigateTo('polls')} class:active={currentPage === 'polls'}>Polls</button>
            <button on:click={() => navigateTo('createPoll')} class:active={currentPage === 'createPoll'}>Create Poll</button>
            <button on:click={logout}>Logout</button>
        {:else}
            <button on:click={() => navigateTo('login')} class:active={currentPage === 'login'}>Login</button>
            <button on:click={() => navigateTo('register')} class:active={currentPage === 'register'}>Register</button>
        {/if}
    </nav>
</header>


<main>
    {#if currentPage === 'home' && currentUser}
        <Home />
    {:else if currentPage === 'polls' && currentUser}
        <Polls />
    {:else if currentPage === 'createPoll' && currentUser}
        <CreatePoll on:pollCreated={() => navigateTo('polls')}/>
    {:else if currentPage === 'login'}
        <Login on:loginSuccess={() => getCurrentUser()} />
    {:else if currentPage === 'register'}
        <Register />
    {/if}
</main>
<Footer/>

{#if error}
    <p style="color: red;">{error}</p>
{/if}


<style>

  /* Style for header with centered content */
.header {
    position: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      background-color: rgba(255, 255, 255, 0.612); /* Slightly transparent background */
      padding: 2rem 1rem;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      color: #333;
      max-width: 8000px;
      margin: auto;
}

/* Center and style the logo */
.logo img {
    height: 200px; /* Adjust size as needed */
    margin-bottom: 3rem;
}

/* General navbar styling */
/* General navbar styling */
nav {
    display: flex;
    gap: 2rem; /* Increase gap for a cleaner look */
}

/* Style for the user info (username) */
span {
    font-weight: bold;
    font-size: 1.1rem;
    color: rgb(3, 3, 3);
    margin-right: auto;
}

/* Style for the buttons in the navbar */
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

/* Underline and red color for the active button */
button.active {
    color: #b00;
    font-weight: bold;
}
span {
    position: absolute;
    top: 1rem;
    left: 1rem;
    font-weight: bold;
    font-size: 1.1rem;
    color: rgb(118, 118, 118);
    
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

/* Hover effect for buttons */
button:hover {
    color: #333;
}


</style>

