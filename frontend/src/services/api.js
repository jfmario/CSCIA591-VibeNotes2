import axios from 'axios'

// Use environment variable or fallback to localhost for development
const API_BASE_URL = process.env.VUE_APP_API_URL || 'http://localhost:8080'
const API_URL = `${API_BASE_URL}/api`

const api = axios.create({
	baseURL: API_URL,
	headers: {
		'Content-Type': 'application/json'
	}
})

// Export the base URL for use in other components
export const BASE_URL = API_BASE_URL

// Add token to requests if available
api.interceptors.request.use(
	(config) => {
		const token = localStorage.getItem('token')
		if (token) {
			config.headers.Authorization = `Bearer ${token}`
		}
		return config
	},
	(error) => {
		return Promise.reject(error)
	}
)

export default {
	// Auth endpoints
	register(username, password) {
		return api.post('/auth/register', { username, password })
	},
	login(username, password) {
		return api.post('/auth/login', { username, password })
	},
	test() {
		return api.get('/auth/test')
	},

	// User endpoints
	getCurrentUserProfile() {
		return api.get('/users/profile')
	},
	updateProfile(description, avatarUrl) {
		return api.put('/users/profile', { description, avatarUrl })
	},
	getAllUsers() {
		return api.get('/users')
	},
	getUserById(id) {
		return api.get(`/users/${id}`)
	},

	// File upload endpoints
	uploadAvatar(file) {
		const formData = new FormData()
		formData.append('file', file)
		return api.post('/upload/avatar', formData, {
			headers: {
				'Content-Type': 'multipart/form-data'
			}
		})
	},

	// Note endpoints
	createNote(title, content, isPublic) {
		return api.post('/notes', { title, content, isPublic })
	},
	getUserNotes() {
		return api.get('/notes')
	},
	getNoteById(id) {
		return api.get(`/notes/${id}`)
	},
	updateNote(id, title, content, isPublic) {
		return api.put(`/notes/${id}`, { title, content, isPublic })
	},
	deleteNote(id) {
		return api.delete(`/notes/${id}`)
	},
	getPublicNotesByUsername(username) {
		return api.get(`/public/users/${username}/notes`)
	},

	// Note attachment endpoints
	uploadNoteAttachment(noteId, file) {
		const formData = new FormData()
		formData.append('file', file)
		return api.post(`/notes/${noteId}/attachments`, formData, {
			headers: {
				'Content-Type': 'multipart/form-data'
			}
		})
	},
	downloadNoteAttachment(noteId, attachmentId) {
		return api.get(`/notes/${noteId}/attachments/${attachmentId}`, {
			responseType: 'blob'
		})
	},
	deleteNoteAttachment(noteId, attachmentId) {
		return api.delete(`/notes/${noteId}/attachments/${attachmentId}`)
	}
}

