<template>
	<div class="notes-page">
		<div class="container">
			<div class="header">
				<h1 class="page-title">My Notes</h1>
				<router-link to="/notes/new" class="btn-create">
					<span class="plus-icon">+</span> Create Note
				</router-link>
			</div>

			<div v-if="loading" class="loading">Loading notes...</div>

			<div v-else-if="error" class="error-message">{{ error }}</div>

			<div v-else-if="notes.length === 0" class="empty-state">
				<div class="empty-icon">📝</div>
				<h2>No notes yet</h2>
				<p>Create your first note to get started!</p>
				<router-link to="/notes/new" class="btn-create-first">Create Note</router-link>
			</div>

			<div v-else class="notes-grid">
				<div v-for="note in notes" :key="note.id" class="note-card" @click="viewNote(note.id)">
					<h3 class="note-title">{{ note.title }}</h3>
					<p class="note-preview">{{ getPreview(note.content) }}</p>
					<div class="note-meta">
						<span class="note-date">{{ formatDate(note.updatedAt) }}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'NotesPage',
	data() {
		return {
			notes: [],
			loading: true,
			error: ''
		}
	},
	mounted() {
		this.loadNotes()
	},
	methods: {
		async loadNotes() {
			this.loading = true
			try {
				const response = await api.getUserNotes()
				this.notes = response.data
			} catch (error) {
				this.error = 'Failed to load notes'
			} finally {
				this.loading = false
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
				month: 'short',
				day: 'numeric',
				hour: '2-digit',
				minute: '2-digit'
			})
		}
	}
}
</script>

<style scoped>
.notes-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
}

.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 3rem;
}

.page-title {
	color: white;
	font-size: 2.5rem;
	font-weight: 700;
	text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.btn-create {
	background: white;
	color: #667eea;
	padding: 0.75rem 1.5rem;
	border-radius: 8px;
	text-decoration: none;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 0.5rem;
	transition: all 0.3s;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-create:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.plus-icon {
	font-size: 1.5rem;
	line-height: 1;
}

.loading {
	text-align: center;
	padding: 3rem;
	color: white;
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

.empty-state {
	background: white;
	border-radius: 12px;
	padding: 4rem 2rem;
	text-align: center;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
	font-size: 4rem;
	margin-bottom: 1rem;
}

.empty-state h2 {
	color: #333;
	font-size: 1.8rem;
	margin-bottom: 0.5rem;
}

.empty-state p {
	color: #666;
	font-size: 1.1rem;
	margin-bottom: 2rem;
}

.btn-create-first {
	display: inline-block;
	background: #667eea;
	color: white;
	padding: 1rem 2rem;
	border-radius: 8px;
	text-decoration: none;
	font-weight: 600;
	transition: all 0.3s;
}

.btn-create-first:hover {
	background: #5568d3;
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.notes-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 2rem;
}

.note-card {
	background: white;
	border-radius: 12px;
	padding: 1.5rem;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
	cursor: pointer;
	transition: all 0.3s;
	display: flex;
	flex-direction: column;
	min-height: 200px;
}

.note-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 30px rgba(102, 126, 234, 0.2);
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
	-webkit-line-clamp: 4;
	-webkit-box-orient: vertical;
}

.note-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 1rem;
	border-top: 1px solid #f0f0f0;
}

.note-date {
	color: #999;
	font-size: 0.85rem;
}

@media (max-width: 768px) {
	.header {
		flex-direction: column;
		align-items: stretch;
		gap: 1rem;
	}

	.btn-create {
		justify-content: center;
	}

	.notes-grid {
		grid-template-columns: 1fr;
	}
}
</style>

