<template>
	<div id="app">
		<nav v-if="isAuthenticated" class="navbar">
			<div class="nav-container">
				<h1 class="logo">VibeNotes</h1>
				<div class="nav-links">
					<router-link to="/dashboard" class="nav-link">Dashboard</router-link>
					<router-link to="/users" class="nav-link">Users</router-link>
					<router-link to="/profile" class="nav-link">Profile</router-link>
					<span class="username">{{ currentUser }}</span>
					<button @click="logout" class="btn-logout">Logout</button>
				</div>
			</div>
		</nav>
		<router-view/>
	</div>
</template>

<script>
export default {
	name: 'App',
	computed: {
		isAuthenticated() {
			return !!localStorage.getItem('token')
		},
		currentUser() {
			return localStorage.getItem('username')
		}
	},
	methods: {
		logout() {
			localStorage.removeItem('token')
			localStorage.removeItem('username')
			this.$router.push('/login')
		}
	}
}
</script>

<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	min-height: 100vh;
}

#app {
	min-height: 100vh;
}

.navbar {
	background: white;
	box-shadow: 0 2px 10px rgba(0,0,0,0.1);
	padding: 1rem 0;
}

.nav-container {
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 2rem;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.logo {
	color: #667eea;
	font-size: 1.5rem;
	font-weight: 700;
}

.nav-links {
	display: flex;
	align-items: center;
	gap: 1.5rem;
}

.nav-link {
	color: #667eea;
	text-decoration: none;
	font-weight: 500;
	transition: color 0.3s;
}

.nav-link:hover {
	color: #5568d3;
}

.nav-link.router-link-active {
	color: #333;
	font-weight: 600;
}

.username {
	color: #333;
	font-weight: 500;
	margin-left: auto;
}

.btn-logout {
	background: #667eea;
	color: white;
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 5px;
	cursor: pointer;
	font-weight: 500;
	transition: background 0.3s;
}

.btn-logout:hover {
	background: #5568d3;
}
</style>

