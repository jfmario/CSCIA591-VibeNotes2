<template>
	<div class="dashboard">
		<div class="container">
			<div class="welcome-card">
				<h1 class="welcome-title">Welcome, {{ username }}! 🎉</h1>
				<p class="welcome-message">You have successfully logged into VibeNotes!</p>
				<p class="info-message">Your note-taking features will appear here soon.</p>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'Dashboard',
	data() {
		return {
			username: localStorage.getItem('username') || 'User'
		}
	}
}
</script>

<style scoped>
.dashboard {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
}

.welcome-card {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
	text-align: center;
}

.welcome-title {
	color: #667eea;
	font-size: 2.5rem;
	font-weight: 700;
	margin-bottom: 1rem;
}

.welcome-message {
	color: #333;
	font-size: 1.2rem;
	margin-bottom: 1rem;
}

.info-message {
	color: #666;
	font-size: 1rem;
}
</style>

