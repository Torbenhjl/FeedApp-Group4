<script>
    import { onMount } from "svelte";
    import keycloak from "./keycloak";
    
    let polls = [];
    let submitting = false;
    let error = '';

    function calculatePercentage(voteCount, totalVotes) {
        return totalVotes ? (voteCount / totalVotes) * 100 : 0;
    }

    // Fetch polls from the backend
    const getPolls = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/polls', {
                headers: {
                    'Authorization': `Bearer ${keycloak.token}`
                 },
                credentials: 'include',
            });
            if (!res.ok) {
                throw new Error(`Failed to fetch polls: ${res.statusText}`);
            }
            polls = await res.json();

            // Ensure vote options are sorted by presentationOrder or id
            polls = polls.map(poll => ({
                ...poll,
                voteOptions: poll.voteOptions.sort((a, b) => a.presentationOrder - b.presentationOrder),
                selectedOptionId: null
            }));
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
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${keycloak.token}`
                 },
                body: JSON.stringify({ optionId: selectedOptionId, pollId: pollId, isUpvote, user: keycloak.tokenParsed?.preferred_username}),
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
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${keycloak.token}`
                     },
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
</script>

<h1>Available Polls</h1>

{#if polls.length === 0}
    <p>Loading polls...</p>
{:else}
    <div class="poll-container">
        {#each polls as poll}
            <div class="poll">
                <h3>{poll.question}</h3>
                <p><strong>Total Votes:</strong> {poll.totalVotes}</p>

                <ul class="options-list">
                    {#each poll.voteOptions as option}
                        <li class="option-item">
                            <label class="pill-radio">
                                <input type="radio" bind:group={poll.selectedOptionId} value={option.id} />
                                <span class="radio-label">{option.caption} ({option.voteCount} votes)</span>
                            </label>
                            <div class="progress-bar-container">
                                <div class="progress-bar" style="width: {calculatePercentage(option.voteCount, poll.voteOptions.reduce((total, opt) => total + opt.voteCount, 0))}%">
                                </div>
                            </div>
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
    .poll-container {
        max-width: 800px;
        margin: auto;
        padding: 2rem;
        background-color: rgba(255, 255, 255, 0.85);
    }

    .poll {
        background: #f9f9f9;
        border: 1px solid #ddd;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        border-radius: 10px;
    }

    .options-list {
        list-style: none;
        padding: 0;
        margin: 1rem 0;
    }

    .option-item {
        margin-bottom: 1rem;
    }

    .progress-bar-container {
        width: 100%;
        background-color: #eee;
        border-radius: 8px;
        overflow: hidden;
        margin-top: 0.5rem;
    }

    .progress-bar {
        height: 20px;
        background-color: #a3efab;
        transition: width 0.3s ease;
    }

    .button-group {
        display: flex;
        gap: 1rem;
        margin-top: 1rem;
    }

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

    .upvote {
        background: linear-gradient(90deg, #5eff00 0%, #a3efab 100%);
        color: #000;
    }

    .downvote {
        background: linear-gradient(90deg, #ff0000 0%, #e98c8c 100%);
        color: #fff;
    }

    .delete-button {
        background-color: #c35151;
        color: white;
    }
    h1 {
        text-align: center;
        font-size: 2.5rem;
        color: #333;
        margin-bottom: 2rem;
    }
    /* Reset box-sizing for all elements */
*,
*:after,
*:before {
  box-sizing: border-box;
}

/* Primary color and text color */
:root {
  --primary-color: #00005c;
  --text-color: #3f3f7d; /* Manually calculated to match the SCSS mix result */
}


label {
  display: flex;
  cursor: pointer;
  font-weight: 500;
  position: relative;
  overflow: hidden;
  margin-bottom: 0.375em;
}

/* Input styling */
label input {
  position: absolute;
  left: -9999px;
}

/* Checked input styling */
label input:checked + span {
  background-color: #d5d5e9; /* Approximation of the SCSS mix result */
}

label input:checked + span:before {
  box-shadow: inset 0 0 0 0.4375em var(--primary-color);
}

/* Span styling */
label span {
  display: flex;
  align-items: center;
  padding: 0.375em 0.75em 0.375em 0.375em;
  border-radius: 99em;
  transition: 0.25s ease;
}

/* Hover effect for span */
label span:hover {
  background-color: #d5d5e9; /* Approximation of the SCSS mix result */
}

/* Before pseudo-element for custom radio button */
label span:before {
  display: flex;
  flex-shrink: 0;
  content: "";
  background-color: #fff;
  width: 1.5em;
  height: 1.5em;
  border-radius: 50%;
  margin-right: 0.375em;
  transition: 0.25s ease;
  box-shadow: inset 0 0 0 0.125em var(--primary-color);
}

/* Centering container (for Codepen preview or demo purposes) */


</style>
