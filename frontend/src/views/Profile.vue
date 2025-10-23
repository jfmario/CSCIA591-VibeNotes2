<template>
	<div class="profile-page">
		<div class="container">
			<div class="profile-card">
				<h1 class="page-title">My Profile</h1>

				<div v-if="loading" class="loading">Loading...</div>

				<div v-else class="profile-content">
					<div class="avatar-section">
						<div class="avatar-preview">
							<img v-if="avatarPreview || profile.avatarUrl" :src="avatarPreview || getAvatarUrl(profile.avatarUrl)" alt="Avatar" class="avatar-image" />
							<div v-else class="avatar-placeholder">
								{{ profile.username ? profile.username[0].toUpperCase() : '?' }}
							</div>
						</div>
						<div class="avatar-input-group">
							<label>Avatar Image</label>
							<div v-if="isEditing" class="upload-section">
								<input
									type="file"
									ref="fileInput"
									@change="handleFileSelect"
									accept="image/*"
									class="file-input"
								/>
								<button @click="triggerFileInput" class="btn-upload" type="button">
									Choose File
								</button>
								<span v-if="selectedFile" class="file-name">{{ selectedFile.name }}</span>
								<span v-else class="file-hint">No file chosen</span>
							</div>
							<div v-else class="current-avatar-info">
								<span v-if="profile.avatarUrl">Avatar set</span>
								<span v-else>No avatar uploaded</span>
							</div>
							<p class="hint">Upload an image file (max 5MB)</p>
							<div v-if="uploadProgress > 0 && uploadProgress < 100" class="progress-bar">
								<div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
							</div>
						</div>
					</div>

					<div class="profile-info">
						<div class="info-group">
							<label>Username</label>
							<div class="info-value">{{ profile.username }}</div>
						</div>

						<div class="info-group">
							<label for="description">Description</label>
							<textarea
								id="description"
								v-model="editedProfile.description"
								placeholder="Tell us about yourself..."
								rows="5"
								:disabled="!isEditing"
							></textarea>
						</div>

						<div class="info-group">
							<label>Member Since</label>
							<div class="info-value">{{ formatDate(profile.createdAt) }}</div>
						</div>
					</div>

					<div v-if="error" class="error-message">{{ error }}</div>
					<div v-if="success" class="success-message">{{ success }}</div>

					<div class="button-group">
						<button v-if="!isEditing" @click="startEditing" class="btn-primary">
							Edit Profile
						</button>
						<template v-else>
							<button @click="saveProfile" class="btn-primary" :disabled="saving">
								{{ saving ? 'Saving...' : 'Save Changes' }}
							</button>
							<button @click="cancelEditing" class="btn-secondary">
								Cancel
							</button>
						</template>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'ProfilePage',
	data() {
		return {
			profile: {},
			editedProfile: {},
			loading: true,
			isEditing: false,
			saving: false,
			error: '',
			success: '',
			selectedFile: null,
			avatarPreview: null,
			uploadProgress: 0
		}
	},
	mounted() {
		this.loadProfile()
	},
	methods: {
		async loadProfile() {
			this.loading = true
			try {
				const response = await api.getCurrentUserProfile()
				this.profile = response.data
				this.editedProfile = {
					description: this.profile.description || '',
					avatarUrl: this.profile.avatarUrl || ''
				}
			} catch (error) {
				this.error = 'Failed to load profile'
			} finally {
				this.loading = false
			}
		},
		startEditing() {
			this.isEditing = true
			this.error = ''
			this.success = ''
			this.selectedFile = null
			this.avatarPreview = null
		},
		cancelEditing() {
			this.isEditing = false
			this.editedProfile = {
				description: this.profile.description || '',
				avatarUrl: this.profile.avatarUrl || ''
			}
			this.error = ''
			this.success = ''
			this.selectedFile = null
			this.avatarPreview = null
			this.uploadProgress = 0
		},
		triggerFileInput() {
			this.$refs.fileInput.click()
		},
		handleFileSelect(event) {
			const file = event.target.files[0]
			if (file) {
				// Validate file size (5MB)
				if (file.size > 5 * 1024 * 1024) {
					this.error = 'File size must be less than 5MB'
					return
				}

				// Validate file type
				if (!file.type.startsWith('image/')) {
					this.error = 'Only image files are allowed'
					return
				}

				this.selectedFile = file
				this.error = ''

				// Create preview
				const reader = new FileReader()
				reader.onload = (e) => {
					this.avatarPreview = e.target.result
				}
				reader.readAsDataURL(file)
			}
		},
		async saveProfile() {
			this.saving = true
			this.error = ''
			this.success = ''

			try {
				let avatarUrl = this.editedProfile.avatarUrl

				// Upload file if selected
				if (this.selectedFile) {
					this.uploadProgress = 10
					const uploadResponse = await api.uploadAvatar(this.selectedFile)
					avatarUrl = 'http://localhost:8080' + uploadResponse.data.url
					this.uploadProgress = 100
				}

				// Update profile
				const response = await api.updateProfile(
					this.editedProfile.description,
					avatarUrl
				)
				this.profile = response.data
				this.isEditing = false
				this.selectedFile = null
				this.avatarPreview = null
				this.uploadProgress = 0
				this.success = 'Profile updated successfully!'
				setTimeout(() => {
					this.success = ''
				}, 3000)
			} catch (error) {
				this.error = error.response?.data?.message || error.response?.data?.error || 'Failed to update profile'
				this.uploadProgress = 0
			} finally {
				this.saving = false
			}
		},
		getAvatarUrl(url) {
			if (!url) return null
			// If URL starts with http, return as is, otherwise prepend backend URL
			if (url.startsWith('http')) {
				return url
			}
			return 'http://localhost:8080' + url
		},
		formatDate(dateString) {
			if (!dateString) return 'N/A'
			const date = new Date(dateString)
			return date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'long',
				day: 'numeric'
			})
		}
	}
}
</script>

<style scoped>
.profile-page {
	min-height: calc(100vh - 70px);
	padding: 3rem 2rem;
}

.container {
	max-width: 800px;
	margin: 0 auto;
}

.profile-card {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.page-title {
	color: #667eea;
	font-size: 2rem;
	font-weight: 700;
	margin-bottom: 2rem;
	text-align: center;
}

.loading {
	text-align: center;
	padding: 2rem;
	color: #666;
}

.profile-content {
	display: flex;
	flex-direction: column;
	gap: 2rem;
}

.avatar-section {
	display: flex;
	align-items: center;
	gap: 2rem;
	padding-bottom: 2rem;
	border-bottom: 2px solid #f0f0f0;
}

.avatar-preview {
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

.avatar-input-group {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
}

.avatar-input-group label {
	color: #333;
	font-weight: 500;
	font-size: 0.95rem;
}

.avatar-input-group input {
	padding: 0.75rem;
	border: 2px solid #e0e0e0;
	border-radius: 8px;
	font-size: 1rem;
	transition: border-color 0.3s;
}

.avatar-input-group input:focus {
	outline: none;
	border-color: #667eea;
}

.avatar-input-group input:disabled {
	background: #f5f5f5;
	cursor: not-allowed;
}

.hint {
	color: #999;
	font-size: 0.85rem;
	margin: 0;
}

.upload-section {
	display: flex;
	align-items: center;
	gap: 1rem;
}

.file-input {
	display: none;
}

.btn-upload {
	background: #667eea;
	color: white;
	border: none;
	padding: 0.5rem 1rem;
	border-radius: 6px;
	cursor: pointer;
	font-weight: 500;
	transition: background 0.3s;
}

.btn-upload:hover {
	background: #5568d3;
}

.file-name {
	color: #333;
	font-size: 0.9rem;
}

.file-hint {
	color: #999;
	font-size: 0.9rem;
	font-style: italic;
}

.current-avatar-info {
	color: #666;
	padding: 0.5rem 0;
}

.progress-bar {
	width: 100%;
	height: 6px;
	background: #e0e0e0;
	border-radius: 3px;
	overflow: hidden;
	margin-top: 0.5rem;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
	transition: width 0.3s ease;
}

.profile-info {
	display: flex;
	flex-direction: column;
	gap: 1.5rem;
}

.info-group {
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
}

.info-group label {
	color: #333;
	font-weight: 500;
	font-size: 0.95rem;
}

.info-value {
	padding: 0.75rem;
	background: #f5f5f5;
	border-radius: 8px;
	color: #666;
}

.info-group textarea {
	padding: 0.75rem;
	border: 2px solid #e0e0e0;
	border-radius: 8px;
	font-size: 1rem;
	font-family: inherit;
	resize: vertical;
	transition: border-color 0.3s;
}

.info-group textarea:focus {
	outline: none;
	border-color: #667eea;
}

.info-group textarea:disabled {
	background: #f5f5f5;
	cursor: not-allowed;
}

.error-message {
	background: #fee;
	color: #c33;
	padding: 0.75rem;
	border-radius: 8px;
	text-align: center;
}

.success-message {
	background: #efe;
	color: #2a2;
	padding: 0.75rem;
	border-radius: 8px;
	text-align: center;
}

.button-group {
	display: flex;
	gap: 1rem;
	justify-content: center;
	margin-top: 1rem;
}

.btn-primary,
.btn-secondary {
	padding: 1rem 2rem;
	border: none;
	border-radius: 8px;
	font-size: 1rem;
	font-weight: 600;
	cursor: pointer;
	transition: all 0.3s;
}

.btn-primary {
	background: #667eea;
	color: white;
}

.btn-primary:hover:not(:disabled) {
	background: #5568d3;
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
	background: #ccc;
	cursor: not-allowed;
	transform: none;
}

.btn-secondary {
	background: #f0f0f0;
	color: #333;
}

.btn-secondary:hover {
	background: #e0e0e0;
}
</style>

