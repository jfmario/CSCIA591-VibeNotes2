<template>
	<div class="users-page">
		<div class="container">
			<h1 class="page-title">Community Members</h1>

			<div v-if="loading" class="loading">Loading users...</div>

			<div v-else-if="error" class="error-message">{{ error }}</div>

			<div v-else class="users-grid">
				<div v-for="user in users" :key="user.id" class="user-card" @click="viewUserProfile(user)">
					<div class="user-avatar">
						<img v-if="user.avatarUrl" :src="getAvatarUrl(user.avatarUrl)" alt="Avatar" class="avatar-image" />
						<div v-else class="avatar-placeholder">
							{{ user.username[0].toUpperCase() }}
						</div>
					</div>
					<div class="user-info">
						<h3 class="user-name">{{ user.username }}</h3>
						<p class="user-description" v-if="user.description">
							{{ user.description }}
						</p>
						<p class="user-description no-description" v-else>
							No description yet
						</p>
						<p class="user-joined">
							Joined {{ formatDate(user.createdAt) }}
						</p>
					</div>
				</div>
			</div>

			<div v-if="!loading && users.length === 0" class="no-users">
				<p>No users found</p>
			</div>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'UsersPage',
	data() {
		return {
			users: [],
			loading: true,
			error: ''
		}
	},
	mounted() {
		this.loadUsers()
	},
	methods: {
		async loadUsers() {
			this.loading = true
			try {
				const response = await api.getAllUsers()
				this.users = response.data
			} catch (error) {
				this.error = 'Failed to load users'
			} finally {
				this.loading = false
			}
		},
		formatDate(dateString) {
			if (!dateString) return 'N/A'
			const date = new Date(dateString)
			return date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'short',
				day: 'numeric'
			})
		},
		getAvatarUrl(url) {
			if (!url) return null
			// If URL starts with http, return as is, otherwise prepend backend URL
			if (url.startsWith('http')) {
				return url
			}
			const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080'
			return baseUrl + url
		},
		viewUserProfile(user) {
			this.$router.push(`/user/${user.id}/${user.username}`)
		}
	}
}
</script>

<style scoped>
.users-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
}

.page-title {
	color: white;
	font-size: 2.5rem;
	font-weight: 700;
	margin-bottom: 3rem;
	text-align: center;
	text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.loading {
	text-align: center;
	padding: 3rem;
	color: #666;
	font-size: 1.2rem;
}

.error-message {
	background: #fee;
	color: #c33;
	padding: 1rem;
	border-radius: 8px;
	text-align: center;
	max-width: 500px;
	margin: 0 auto;
}

.users-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 2rem;
}

.user-card {
	background: white;
	border-radius: 12px;
	padding: 2rem;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
	transition: transform 0.3s, box-shadow 0.3s;
	display: flex;
	flex-direction: column;
	align-items: center;
	text-align: center;
}

.user-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 30px rgba(102, 126, 234, 0.2);
}

.user-avatar {
	margin-bottom: 1.5rem;
}

.avatar-image {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	object-fit: cover;
	border: 4px solid #667eea;
}

.avatar-placeholder {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 2.5rem;
	font-weight: 700;
}

.user-info {
	width: 100%;
}

.user-name {
	color: #333;
	font-size: 1.5rem;
	font-weight: 600;
	margin-bottom: 0.5rem;
}

.user-description {
	color: #666;
	line-height: 1.6;
	margin-bottom: 1rem;
	min-height: 3rem;
}

.no-description {
	color: #999;
	font-style: italic;
}

.user-joined {
	color: #999;
	font-size: 0.85rem;
	margin-top: auto;
}

.no-users {
	text-align: center;
	padding: 3rem;
	color: #666;
	font-size: 1.1rem;
}

@media (max-width: 768px) {
	.users-grid {
		grid-template-columns: 1fr;
	}
}
</style>

