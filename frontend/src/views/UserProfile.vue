<template>
	<div class="user-profile-page">
		<div class="container">
			<div v-if="loading" class="loading">Loading...</div>

			<div v-else>
				<div class="profile-header">
					<div class="user-avatar">
						<img v-if="user.avatarUrl" :src="getAvatarUrl(user.avatarUrl)" alt="Avatar" class="avatar-image" />
						<div v-else class="avatar-placeholder">
							{{ user.username[0].toUpperCase() }}
						</div>
					</div>
					<div class="user-info">
						<h1 class="user-name">{{ user.username }}</h1>
						<p v-if="user.description" class="user-description">{{ user.description }}</p>
						<p v-else class="user-description no-description">No description yet</p>
						<p class="user-joined">Member since {{ formatDate(user.createdAt) }}</p>
					</div>
				</div>

				<div class="public-notes-section">
					<h2 class="section-title">📚 Public Notes</h2>

					<div v-if="loadingNotes" class="loading-notes">Loading notes...</div>

					<div v-else-if="publicNotes.length === 0" class="no-notes">
						<p>{{ user.username }} hasn't shared any public notes yet.</p>
					</div>

					<div v-else class="notes-grid">
						<div v-for="note in publicNotes" :key="note.id" class="note-card" @click="viewNote(note.id)">
							<h3 class="note-title">{{ note.title }}</h3>
							<p class="note-preview">{{ getPreview(note.content) }}</p>
							<div class="note-meta">
								<span class="note-date">{{ formatNoteDate(note.updatedAt) }}</span>
								<span v-if="note.attachments && note.attachments.length > 0" class="note-attachments">
									📎 {{ note.attachments.length }}
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'UserProfilePage',
	data() {
		return {
			user: {},
			publicNotes: [],
			loading: true,
			loadingNotes: true
		}
	},
	mounted() {
		this.loadUserProfile()
		this.loadPublicNotes()
	},
	methods: {
		async loadUserProfile() {
			this.loading = true
			try {
				const response = await api.getUserById(this.$route.params.userId)
				this.user = response.data
			} catch (error) {
				console.error('Failed to load user profile', error)
			} finally {
				this.loading = false
			}
		},
		async loadPublicNotes() {
			this.loadingNotes = true
			try {
				const response = await api.getPublicNotesByUsername(this.$route.params.username)
				this.publicNotes = response.data
			} catch (error) {
				console.error('Failed to load public notes', error)
			} finally {
				this.loadingNotes = false
			}
		},
		viewNote(id) {
			this.$router.push(`/notes/${id}`)
		},
		getPreview(content) {
			if (!content) return ''
			return content.length > 150 ? content.substring(0, 150) + '...' : content
		},
		formatDate(dateString) {
			if (!dateString) return ''
			const date = new Date(dateString)
			return date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'long'
			})
		},
		formatNoteDate(dateString) {
			if (!dateString) return ''
			const date = new Date(dateString)
			return date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'short',
				day: 'numeric'
			})
		},
		getAvatarUrl(url) {
			if (!url) return null
			if (url.startsWith('http')) {
				return url
			}
			const baseUrl = process.env.VUE_APP_API_URL || 'http://localhost:8080'
			return baseUrl + url
		}
	}
}
</script>

<style scoped>
.user-profile-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
}

.loading {
	text-align: center;
	padding: 3rem;
	color: white;
	font-size: 1.2rem;
}

.profile-header {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
	display: flex;
	gap: 2rem;
	align-items: center;
	margin-bottom: 3rem;
}

.user-avatar {
	flex-shrink: 0;
}

.avatar-image {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	object-fit: cover;
	border: 4px solid #667eea;
}

.avatar-placeholder {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 3rem;
	font-weight: 700;
}

.user-info {
	flex: 1;
}

.user-name {
	color: #333;
	font-size: 2.5rem;
	font-weight: 700;
	margin-bottom: 0.5rem;
}

.user-description {
	color: #666;
	font-size: 1.1rem;
	line-height: 1.6;
	margin-bottom: 1rem;
}

.no-description {
	color: #999;
	font-style: italic;
}

.user-joined {
	color: #999;
	font-size: 0.9rem;
}

.public-notes-section {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
	color: #333;
	font-size: 2rem;
	font-weight: 700;
	margin-bottom: 2rem;
}

.loading-notes {
	text-align: center;
	padding: 2rem;
	color: #666;
}

.no-notes {
	text-align: center;
	padding: 3rem;
	color: #666;
	font-size: 1.1rem;
}

.notes-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 2rem;
}

.note-card {
	background: #f8f9fa;
	border-radius: 12px;
	padding: 1.5rem;
	cursor: pointer;
	transition: all 0.3s;
	display: flex;
	flex-direction: column;
	min-height: 180px;
	border: 2px solid transparent;
}

.note-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 30px rgba(102, 126, 234, 0.2);
	border-color: #667eea;
}

.note-title {
	color: #333;
	font-size: 1.3rem;
	font-weight: 600;
	margin-bottom: 0.75rem;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.note-preview {
	color: #666;
	line-height: 1.6;
	flex: 1;
	margin-bottom: 1rem;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
}

.note-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 1rem;
	border-top: 1px solid #e0e0e0;
}

.note-date {
	color: #999;
	font-size: 0.85rem;
}

.note-attachments {
	color: #667eea;
	font-size: 0.85rem;
	font-weight: 500;
}

@media (max-width: 768px) {
	.profile-header {
		flex-direction: column;
		text-align: center;
	}

	.notes-grid {
		grid-template-columns: 1fr;
	}
}
</style>

