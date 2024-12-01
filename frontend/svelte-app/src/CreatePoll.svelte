<script>
    import { createEventDispatcher } from "svelte";
  import keycloak from "./keycloak";

    let question = '';
    let voteOptions = [''];
    let validUntil = '';  
    let isPrivate = false;  
    let currentPage = 'createPoll';
    let user = '';

    const dispatch = createEventDispatcher();

    const addOption = () => {
        voteOptions = [...voteOptions, ''];
    };

    const isValidDate = (localDateTime) => {
        const dateTime = new Date(localDateTime);
        if(isNaN(dateTime)) {
            return false;
        }
        const now = new Date();
        return dateTime >= now;
    }
    const removeOption = (index) => {
        if (voteOptions.length > 1) {
            voteOptions = voteOptions.filter((_, i) => i !== index);
        } else {
            alert("At least one option is required.");
        }
    };

    const submitPoll = async () => {
    console.log("Submit poll running");
    user = keycloak.tokenParsed?.preferred_username

        if (!question || !validUntil || voteOptions.some(option => option === '' || !user)) {
        alert('Please fill out all fields before submitting.');
        return;
    }
    if (!isValidDate(validUntil)) {
        console.log("Invalid date. No time travel allowed.");
        return;
    }

    const options = voteOptions.map((option, index) => ({
        caption: option,
        presentationOrder: index + 1,
    }));

    const pollData = {
        question,
        voteOptions: options,
        validUntil,
        isPrivate,
        user: keycloak.tokenParsed?.preferred_username
    };

    try {
        const res = await fetch('http://localhost:8080/api/polls', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${keycloak.token}`,
            },
            body: JSON.stringify(pollData),
            credentials: 'include',
        });

        if (res.ok) {
            alert('Poll created successfully');
            dispatch('pollCreated');
        } else {
            const errorMessage = await res.text();
            alert(`Failed to create poll: ${errorMessage}`);
        }
    } catch (error) {
        console.error('Error submitting poll:', error);
        alert('An error occurred while creating the poll.');
    }
};

</script>

<h1>Create a Poll</h1>

<div class="poll-form">
    <div class="form-group">
        <label for="question">Poll Question:</label>
        <input id="question" placeholder="Poll Question" bind:value={question} />
    </div>

    {#each voteOptions as option, index}
        <div class="form-group option-group">
            <label for="option-{index}">Option:</label>
            <input id="option-{index}" placeholder="Option" bind:value={voteOptions[index]} />
            <button class="remove-option-button" on:click={() => removeOption(index)}>Remove</button>
        </div>
    {/each}

    <button class="add-option-button" on:click={addOption}>Add Option</button>

    <!-- Add the Valid Until date input field back -->
    <div class="form-group">
        <label for="validUntil">Valid Until:</label>
        <input id="validUntil" type="datetime-local" bind:value={validUntil} />
    </div>

    <div class="form-group">
        <label for="isPrivate">Private Poll:</label>
        <div class="toggle-container">
            <input type="checkbox" id="isPrivate" bind:checked={isPrivate} class="toggle-checkbox" />
            <label for="isPrivate" class="toggle-switch"></label>
        </div>
    </div>

    <button class="submit-button" on:click={submitPoll}>Submit Poll</button>
</div>

<style>
.poll-form {
    max-width: 600px;
    margin: auto;
    padding: 2rem;
    background-color: rgba(255, 255, 255, 0.85);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.form-group {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;
}

label {
    font-weight: bold;
    flex-shrink: 0;
    width: 120px;
    text-align: right;
}

input[type="text"],
input[type="datetime-local"] {
    flex-grow: 1;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.add-option-button,
.submit-button {
    background-color: #007bff;
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    align-self: flex-start;
    transition: background-color 0.2s ease;
}

.add-option-button:hover,
.submit-button:hover {
    background-color: #0056b3;
}

.remove-option-button {
    background-color: #ff4d4d;
    color: white;
    border: none;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.remove-option-button:hover {
    background-color: #cc0000;
}

.toggle-container {
    display: inline-flex;
    align-items: center;
    position: relative;
    cursor: pointer;
}

.toggle-checkbox {
    display: none;
}

.toggle-switch {
    width: 40px;
    height: 20px;
    background-color: #ddd;
    border-radius: 99em;
    position: relative;
    transition: background-color 0.25s ease;
}

.toggle-switch::before {
    content: '';
    width: 18px;
    height: 18px;
    background-color: white;
    border-radius: 50%;
    position: absolute;
    top: 1px;
    left: 1px;
    transition: 0.25s ease;

    cursor: pointer;
}

.toggle-checkbox:checked + .toggle-switch {
    background-color: #a3efab;
    cursor: pointer;
}

.toggle-checkbox:checked + .toggle-switch::before {
    transform: translateX(20px);
    cursor: pointer;
}

h1 {
    text-align: center;
}
</style>
