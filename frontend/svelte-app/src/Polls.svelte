<script>
    import { onMount } from "svelte";
    
    let polls = [];
    let submitting = false;
    let error = '';

    // Fetch polls from the backend

    const getPolls = async () => {
    try {
        const res = await fetch('http://localhost:8080/api/polls', {
            credentials: 'include',
        });
        if (!res.ok) {
            throw new Error(`Failed to fetch polls: ${res.statusText}`);
        }
        polls = await res.json();

        // Log the poll data to check voteCount values
        console.log("Fetched Polls Data:", polls);

        polls = polls.map(poll => ({ ...poll, selectedOptionId: null }));
    } catch (error) {
        console.error('Error fetching polls:', error);
    }
};


    // Submit a vote
    const submitVote = async (pollId, selectedOptionId, isUpvote) => {
        if (!selectedOptionId) {
            error = 'Please select an option before submitting your vote.';
            return;
        }

        try {
            submitting = true;
            error = '';

            const res = await fetch('http://localhost:8080/api/votes', { 
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ optionId: selectedOptionId, pollId: pollId, isUpvote }),
                credentials: 'include'
            });

            if (res.ok) {
                await getPolls();  // Refresh polls to get updated vote counts directly from backend
            } else {
                const errorMessage = await res.text();
                alert("Failed to submit vote: " + errorMessage);
            }
        } catch (error) {
            console.error('Error submitting vote:', error);
            alert('An error occurred while submitting your vote.');
        } finally {
            submitting = false;
        }
    };

    
    const deletePoll = async (pollId) => {
        if (confirm("Are you sure you want to delete this poll?")) {
            try {
                const res = await fetch(`http://localhost:8080/api/polls/${pollId}`, {
                    method: 'DELETE',
                    credentials: 'include'
                });

                if (res.ok) {
                    alert("Poll deleted successfully!");
                    getPolls();  // Refresh polls after deletion
                } else {
                    alert("Failed to delete poll.");
                }
            } catch (error) {
                console.error('Error deleting poll:', error);
                alert('An error occurred while deleting the poll.');
            }
        }
    };

    onMount(() => {
        getPolls();
    });

    onMount(() => {
        getPolls();
    });
</script>

<h1>Available Polls</h1>

{#if polls.length === 0}
    <p>Loading polls...</p>
{:else}
    <div class="poll-container">
        {#each polls as poll}
            <div class="poll">
                <h3>{poll.question}</h3>
                <p class="poll-status"><strong>Status:</strong> {poll.isPrivate ? 'Private' : 'Public'}</p>
                <p><strong>Published At:</strong> {new Date(poll.publishedAt).toLocaleString()}</p>
                <p><strong>Valid Until:</strong> {poll.validUntil ? new Date(poll.validUntil).toLocaleString() : 'No expiration date'}</p>
                <ul class="options-list">
                    {#each poll.voteOptions as option}
                        <li>
                            <label>
                                <input type="radio" bind:group={poll.selectedOptionId} value={option.id} />
                                {option.caption} ({option.voteCount} votes) <!-- Directly displaying voteCount from backend -->
                            </label>
                        </li>
                    {/each}
                </ul>
                <div class="button-group">
                    <button on:click={() => submitVote(poll.id, poll.selectedOptionId, true)} disabled={submitting} class="upvote">
                        {submitting ? 'Submitting...' : 'Upvote'}
                    </button>
                    <button on:click={() => submitVote(poll.id, poll.selectedOptionId, false)} disabled={submitting} class="downvote">
                        {submitting ? 'Submitting...' : 'Downvote'}
                    </button>
                    <button on:click={() => deletePoll(poll.id)} class="delete-button">
                        Delete Poll
                    </button>
                </div>
            </div>
        {/each}
    </div>
{/if}




<style>
    /* Center poll container */
    .poll-container {
        max-width: 800px;
        margin: auto;
        padding: 2rem;
        border-radius: 8px;
        background-color: rgba(255, 255, 255, 0.85);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    /* Title styling */
    h1 {
        text-align: center;
        font-size: 2.5rem;
        color: #333;
        margin-bottom: 2rem;
    }

    /* Poll card styling */
    .poll {
        background: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 10px;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    /* Styling for poll question */
    h3 {
        font-size: 1.5rem;
        color: #333;
        margin-bottom: 0.5rem;
    }

    /* Poll status styling */
    .poll-status {
        font-weight: bold;
        margin-bottom: 1rem;
    }

    /* Options list styling */
    .options-list {
        list-style: none;
        padding: 0;
        margin: 1rem 0;
    }

    .options-list li {
        margin-bottom: 0.5rem;
    }

    /* Button group styling */
    .button-group {
        display: flex;
        gap: 1rem;
        margin-top: 1rem;
    }

    /* Styling for buttons */
    button {
        font-size: 1rem;
        padding: 0.5rem 1.5rem;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        transition: transform 0.1s ease;
    }

    button:hover {
        transform: scale(1.05);
    }

    button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    /* Upvote button */
    .upvote {
        background: linear-gradient(90deg, #5eff00 0%, #a3efab 100%);
        color: #000;
    }

    /* Downvote button */
    .downvote {
        background: linear-gradient(90deg, #ff0000 0%, #e98c8c 100%);
        color: #fff;
    }

    /* Delete button */
    .delete-button {
        background-color: #c35151;
        color: white;
    }

    /* Error message styling */
    .error-message {
        color: red;
        text-align: center;
        margin-top: 1rem;
        font-weight: bold;
    }
</style>
