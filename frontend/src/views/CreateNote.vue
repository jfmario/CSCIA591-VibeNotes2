<template>
	<div class="create-note-page">
		<div class="container">
			<div class="note-card">
				<div class="header">
					<h1 class="page-title">Create New Note</h1>
					<button @click="goBack" class="btn-back">← Back</button>
				</div>

				<form @submit.prevent="handleSubmit" class="note-form">
					<div class="form-group">
						<label for="title">Title</label>
						<input
							type="text"
							id="title"
							v-model="note.title"
							placeholder="Enter note title..."
							required
							maxlength="200"
						/>
						<span class="char-count">{{ note.title.length }}/200</span>
					</div>

					<div class="form-group">
						<label for="content">Content</label>
						<textarea
							id="content"
							v-model="note.content"
							placeholder="Write your note here..."
							rows="15"
							required
						></textarea>
					</div>

					<div v-if="error" class="error-message">{{ error }}</div>

					<div class="button-group">
						<button type="submit" class="btn-save" :disabled="saving">
							{{ saving ? 'Saving...' : 'Save Note' }}
						</button>
						<button type="button" @click="goBack" class="btn-cancel">
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
	name: 'CreateNotePage',
	data() {
		return {
			note: {
				title: '',
				content: ''
			},
			saving: false,
			error: ''
		}
	},
	methods: {
		async handleSubmit() {
			this.saving = true
			this.error = ''

			try {
				await api.createNote(this.note.title, this.note.content)
				this.$router.push('/notes')
			} catch (error) {
				this.error = error.response?.data?.message || 'Failed to create note'
			} finally {
				this.saving = false
			}
		},
		goBack() {
			this.$router.push('/notes')
		}
	}
}
</script>

<style scoped>
.create-note-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 900px;
	margin: 0 auto;
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

.page-title {
	color: #667eea;
	font-size: 2rem;
	font-weight: 700;
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

.note-form {
	display: flex;
	flex-direction: column;
	gap: 1.5rem;
}

.form-group {
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
	position: relative;
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

.char-count {
	position: absolute;
	top: 2rem;
	right: 0.5rem;
	color: #999;
	font-size: 0.85rem;
}

.error-message {
	background: #fee;
	color: #c33;
	padding: 0.75rem;
	border-radius: 8px;
	text-align: center;
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

@media (max-width: 768px) {
	.note-card {
		padding: 2rem 1.5rem;
	}

	.header {
		flex-direction: column;
		align-items: stretch;
		gap: 1rem;
	}

	.button-group {
		flex-direction: column;
	}
}
</style>

