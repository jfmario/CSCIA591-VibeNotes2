<template>
	<div class="dashboard">
		<div class="container">
			<div class="welcome-card">
				<h1 class="welcome-title">Welcome, {{ username }}! 🎉</h1>
				<p class="welcome-message">You have successfully logged into VibeNotes!</p>
				<div class="quick-actions">
					<router-link to="/notes" class="action-card">
						<span class="action-icon">📝</span>
						<h3>My Notes</h3>
						<p>Create and manage your notes</p>
					</router-link>
					<router-link to="/users" class="action-card">
						<span class="action-icon">👥</span>
						<h3>Community</h3>
						<p>View other members</p>
					</router-link>
					<router-link to="/profile" class="action-card">
						<span class="action-icon">⚙️</span>
						<h3>Profile</h3>
						<p>Edit your profile</p>
					</router-link>
				</div>
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

.quick-actions {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	gap: 2rem;
	margin-top: 3rem;
}

.action-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	padding: 2rem;
	border-radius: 12px;
	text-decoration: none;
	transition: all 0.3s;
	text-align: center;
}

.action-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 30px rgba(102, 126, 234, 0.3);
}

.action-icon {
	font-size: 3rem;
	display: block;
	margin-bottom: 1rem;
}

.action-card h3 {
	font-size: 1.5rem;
	margin-bottom: 0.5rem;
}

.action-card p {
	opacity: 0.9;
}
</style>

