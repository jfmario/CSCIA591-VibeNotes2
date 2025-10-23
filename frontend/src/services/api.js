import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

const api = axios.create({
	baseURL: API_URL,
	headers: {
		'Content-Type': 'application/json'
	}
})

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
	}
}

