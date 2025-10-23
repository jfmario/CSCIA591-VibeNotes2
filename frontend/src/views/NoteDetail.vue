<template>
	<div class="note-detail-page">
		<div class="container">
			<div v-if="loading" class="loading">Loading note...</div>

			<div v-else-if="error" class="error-message">{{ error }}</div>

			<div v-else class="note-card">
				<div class="header">
					<button @click="goBack" class="btn-back">← Back to Notes</button>
					<div class="actions">
						<button @click="toggleEdit" class="btn-edit" v-if="!isEditing">
							✏️ Edit
						</button>
						<button @click="handleDelete" class="btn-delete" v-if="!isEditing">
							🗑️ Delete
						</button>
					</div>
				</div>

				<div v-if="!isEditing" class="note-view">
					<h1 class="note-title">{{ note.title }}</h1>
					<div class="note-meta">
						<span>Created: {{ formatDate(note.createdAt) }}</span>
						<span>Updated: {{ formatDate(note.updatedAt) }}</span>
					</div>
					<div class="note-content">{{ note.content }}</div>
					
					<div v-if="note.attachments && note.attachments.length > 0" class="attachments-section">
						<h3 class="attachments-title">📎 Attachments ({{ note.attachments.length }})</h3>
						<div class="attachments-list">
							<div v-for="attachment in note.attachments" :key="attachment.id" class="attachment-item">
								<div class="attachment-info">
									<span class="attachment-name">{{ attachment.originalFilename }}</span>
									<span class="attachment-size">{{ formatFileSize(attachment.fileSize) }}</span>
								</div>
								<div class="attachment-actions">
									<button @click="downloadAttachment(attachment)" class="btn-download">
										⬇️ Download
									</button>
									<button @click="deleteAttachment(attachment.id)" class="btn-delete-attachment">
										🗑️
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>

				<form v-else @submit.prevent="handleUpdate" class="note-form">
					<div class="form-group">
						<label for="title">Title</label>
						<input
							type="text"
							id="title"
							v-model="editedNote.title"
							required
							maxlength="200"
						/>
					</div>

					<div class="form-group">
						<label for="content">Content</label>
						<textarea
							id="content"
							v-model="editedNote.content"
							rows="15"
							required
						></textarea>
					</div>

					<div class="form-group">
						<label class="checkbox-label">
							<input type="checkbox" v-model="editedNote.isPublic" class="checkbox-input" />
							<span class="checkbox-text">🌐 Make this note public (visible on your profile)</span>
						</label>
					</div>

					<div v-if="updateError" class="error-message">{{ updateError }}</div>

					<div class="button-group">
						<button type="submit" class="btn-save" :disabled="updating">
							{{ updating ? 'Saving...' : 'Save Changes' }}
						</button>
						<button type="button" @click="cancelEdit" class="btn-cancel">
							Cancel
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'NoteDetailPage',
	data() {
		return {
			note: null,
			editedNote: {
				title: '',
				content: '',
				isPublic: false
			},
			loading: true,
			error: '',
			isEditing: false,
			updating: false,
			updateError: ''
		}
	},
	mounted() {
		this.loadNote()
	},
	methods: {
		async loadNote() {
			this.loading = true
			try {
				const response = await api.getNoteById(this.$route.params.id)
				this.note = response.data
				this.editedNote = {
					title: this.note.title,
					content: this.note.content,
					isPublic: this.note.isPublic || false
				}
			} catch (error) {
				this.error = 'Failed to load note'
			} finally {
				this.loading = false
			}
		},
		toggleEdit() {
			this.isEditing = true
			this.updateError = ''
		},
		cancelEdit() {
			this.isEditing = false
			this.editedNote = {
				title: this.note.title,
				content: this.note.content,
				isPublic: this.note.isPublic || false
			}
			this.updateError = ''
		},
		async handleUpdate() {
			this.updating = true
			this.updateError = ''

			try {
				const response = await api.updateNote(
					this.$route.params.id,
					this.editedNote.title,
					this.editedNote.content,
					this.editedNote.isPublic
				)
				this.note = response.data
				this.isEditing = false
			} catch (error) {
				this.updateError = error.response?.data?.message || 'Failed to update note'
			} finally {
				this.updating = false
			}
		},
		async handleDelete() {
			if (!confirm('Are you sure you want to delete this note?')) {
				return
			}

			try {
				await api.deleteNote(this.$route.params.id)
				this.$router.push('/notes')
			} catch (error) {
				this.error = 'Failed to delete note'
			}
		},
		goBack() {
			this.$router.push('/notes')
		},
		formatDate(dateString) {
			if (!dateString) return ''
			const date = new Date(dateString)
			return date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'long',
				day: 'numeric',
				hour: '2-digit',
				minute: '2-digit'
			})
		},
		formatFileSize(bytes) {
			if (bytes === 0) return '0 B'
			const k = 1024
			const sizes = ['B', 'KB', 'MB', 'GB']
			const i = Math.floor(Math.log(bytes) / Math.log(k))
			return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
		},
		async downloadAttachment(attachment) {
			try {
				const response = await api.downloadNoteAttachment(this.$route.params.id, attachment.id)
				
				// Create blob and download
				const url = window.URL.createObjectURL(new Blob([response.data]))
				const link = document.createElement('a')
				link.href = url
				link.setAttribute('download', attachment.originalFilename)
				document.body.appendChild(link)
				link.click()
				link.remove()
				window.URL.revokeObjectURL(url)
			} catch (error) {
				this.error = 'Failed to download attachment'
			}
		},
		async deleteAttachment(attachmentId) {
			if (!confirm('Are you sure you want to delete this attachment?')) {
				return
			}

			try {
				await api.deleteNoteAttachment(this.$route.params.id, attachmentId)
				// Reload note to refresh attachments list
				await this.loadNote()
			} catch (error) {
				this.error = 'Failed to delete attachment'
			}
		}
	}
}
</script>

<style scoped>
.note-detail-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 900px;
	margin: 0 auto;
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
}

.note-card {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 2rem;
}

.btn-back {
	background: #f0f0f0;
	color: #333;
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 6px;
	cursor: pointer;
	font-weight: 500;
	transition: background 0.3s;
}

.btn-back:hover {
	background: #e0e0e0;
}

.actions {
	display: flex;
	gap: 0.5rem;
}

.btn-edit,
.btn-delete {
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 6px;
	cursor: pointer;
	font-weight: 500;
	transition: all 0.3s;
}

.btn-edit {
	background: #667eea;
	color: white;
}

.btn-edit:hover {
	background: #5568d3;
}

.btn-delete {
	background: #ff6b6b;
	color: white;
}

.btn-delete:hover {
	background: #ff5252;
}

.note-view {
	display: flex;
	flex-direction: column;
	gap: 1.5rem;
}

.note-title {
	color: #333;
	font-size: 2.5rem;
	font-weight: 700;
	line-height: 1.3;
}

.note-meta {
	display: flex;
	gap: 2rem;
	color: #999;
	font-size: 0.9rem;
	padding-bottom: 1rem;
	border-bottom: 2px solid #f0f0f0;
}

.note-content {
	color: #444;
	font-size: 1.1rem;
	line-height: 1.8;
	white-space: pre-wrap;
	word-wrap: break-word;
}

.note-form {
	display: flex;
	flex-direction: column;
	gap: 1.5rem;
}

.form-group {
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
}

.form-group label {
	color: #333;
	font-weight: 500;
	font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
	padding: 0.75rem;
	border: 2px solid #e0e0e0;
	border-radius: 8px;
	font-size: 1rem;
	font-family: inherit;
	transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
	outline: none;
	border-color: #667eea;
}

.form-group textarea {
	resize: vertical;
	min-height: 300px;
}

.button-group {
	display: flex;
	gap: 1rem;
	margin-top: 1rem;
}

.btn-save,
.btn-cancel {
	flex: 1;
	padding: 1rem;
	border: none;
	border-radius: 8px;
	font-size: 1rem;
	font-weight: 600;
	cursor: pointer;
	transition: all 0.3s;
}

.btn-save {
	background: #667eea;
	color: white;
}

.btn-save:hover:not(:disabled) {
	background: #5568d3;
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-save:disabled {
	background: #ccc;
	cursor: not-allowed;
	transform: none;
}

.btn-cancel {
	background: #f0f0f0;
	color: #333;
}

.btn-cancel:hover {
	background: #e0e0e0;
}

.attachments-section {
	margin-top: 2rem;
	padding-top: 2rem;
	border-top: 2px solid #f0f0f0;
}

.attachments-title {
	color: #333;
	font-size: 1.3rem;
	margin-bottom: 1rem;
}

.attachments-list {
	display: flex;
	flex-direction: column;
	gap: 0.75rem;
}

.attachment-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 1rem;
	background: #f8f9fa;
	border-radius: 8px;
	border: 1px solid #e0e0e0;
}

.attachment-info {
	display: flex;
	flex-direction: column;
	gap: 0.25rem;
	flex: 1;
}

.attachment-name {
	color: #333;
	font-weight: 500;
}

.attachment-size {
	color: #666;
	font-size: 0.85rem;
}

.attachment-actions {
	display: flex;
	gap: 0.5rem;
}

.btn-download {
	background: #667eea;
	color: white;
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 6px;
	cursor: pointer;
	font-weight: 500;
	transition: background 0.3s;
}

.btn-download:hover {
	background: #5568d3;
}

.btn-delete-attachment {
	background: #ff6b6b;
	color: white;
	border: none;
	padding: 0.5rem 0.75rem;
	border-radius: 6px;
	cursor: pointer;
	font-size: 1rem;
	transition: background 0.3s;
}

.btn-delete-attachment:hover {
	background: #ff5252;
}

.checkbox-label {
	display: flex;
	align-items: center;
	gap: 0.75rem;
	cursor: pointer;
	padding: 1rem;
	background: #f8f9fa;
	border-radius: 8px;
	border: 2px solid #e0e0e0;
	transition: all 0.3s;
}

.checkbox-label:hover {
	border-color: #667eea;
	background: #f0f4ff;
}

.checkbox-input {
	width: 20px;
	height: 20px;
	cursor: pointer;
}

.checkbox-text {
	color: #333;
	font-weight: 500;
}

@media (max-width: 768px) {
	.note-card {
		padding: 2rem 1.5rem;
	}

	.header {
		flex-direction: column;
		align-items: stretch;
		gap: 1rem;
	}

	.actions {
		justify-content: center;
	}

	.note-title {
		font-size: 2rem;
	}

	.note-meta {
		flex-direction: column;
		gap: 0.5rem;
	}

	.button-group {
		flex-direction: column;
	}
}
</style>

