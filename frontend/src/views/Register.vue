<template>
	<div class="auth-container">
		<div class="auth-card">
			<h1 class="title">Join VibeNotes</h1>
			<p class="subtitle">Create your account</p>

			<form @submit.prevent="handleRegister" class="auth-form">
				<div class="form-group">
					<label for="username">Username</label>
					<input
						type="text"
						id="username"
						v-model="username"
						placeholder="Choose a username (min 3 characters)"
						required
						minlength="3"
					/>
				</div>

				<div class="form-group">
					<label for="password">Password</label>
					<input
						type="password"
						id="password"
						v-model="password"
						placeholder="Choose a password (min 6 characters)"
						required
						minlength="6"
					/>
				</div>

				<div class="form-group">
					<label for="confirmPassword">Confirm Password</label>
					<input
						type="password"
						id="confirmPassword"
						v-model="confirmPassword"
						placeholder="Confirm your password"
						required
					/>
				</div>

				<div v-if="error" class="error-message">
					{{ error }}
				</div>

				<button type="submit" class="btn-submit" :disabled="loading">
					{{ loading ? 'Creating account...' : 'Register' }}
				</button>
			</form>

			<p class="auth-link">
				Already have an account?
				<router-link to="/login">Login here</router-link>
			</p>
		</div>
	</div>
</template>

<script>
import api from '../services/api'

export default {
	name: 'Register',
	data() {
		return {
			username: '',
			password: '',
			confirmPassword: '',
			error: '',
			loading: false
		}
	},
	methods: {
		async handleRegister() {
			this.error = ''

			// Validation
			if (this.username.length < 3) {
				this.error = 'Username must be at least 3 characters'
				return
			}

			if (this.password.length < 6) {
				this.error = 'Password must be at least 6 characters'
				return
			}

			if (this.password !== this.confirmPassword) {
				this.error = 'Passwords do not match'
				return
			}

			this.loading = true

			try {
				const response = await api.register(this.username, this.password)
				
				// Store token and username
				localStorage.setItem('token', response.data.token)
				localStorage.setItem('username', response.data.username)

				// Redirect to dashboard
				this.$router.push('/dashboard')
			} catch (error) {
				this.error = error.response?.data?.message || 'Registration failed. Please try again.'
			} finally {
				this.loading = false
			}
		}
	}
}
</script>

<style scoped>
.auth-container {
	min-height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 2rem;
}

.auth-card {
	background: white;
	border-radius: 12px;
	padding: 3rem;
	width: 100%;
	max-width: 450px;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.title {
	color: #667eea;
	font-size: 2rem;
	font-weight: 700;
	margin-bottom: 0.5rem;
	text-align: center;
}

.subtitle {
	color: #666;
	text-align: center;
	margin-bottom: 2rem;
}

.auth-form {
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

.form-group input {
	padding: 0.75rem;
	border: 2px solid #e0e0e0;
	border-radius: 8px;
	font-size: 1rem;
	transition: border-color 0.3s;
}

.form-group input:focus {
	outline: none;
	border-color: #667eea;
}

.error-message {
	background: #fee;
	color: #c33;
	padding: 0.75rem;
	border-radius: 8px;
	font-size: 0.9rem;
	text-align: center;
}

.btn-submit {
	background: #667eea;
	color: white;
	border: none;
	padding: 1rem;
	border-radius: 8px;
	font-size: 1rem;
	font-weight: 600;
	cursor: pointer;
	transition: background 0.3s;
}

.btn-submit:hover:not(:disabled) {
	background: #5568d3;
}

.btn-submit:disabled {
	background: #ccc;
	cursor: not-allowed;
}

.auth-link {
	text-align: center;
	margin-top: 1.5rem;
	color: #666;
}

.auth-link a {
	color: #667eea;
	text-decoration: none;
	font-weight: 600;
}

.auth-link a:hover {
	text-decoration: underline;
}
</style>

