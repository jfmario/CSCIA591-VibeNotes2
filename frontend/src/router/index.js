import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import Profile from '../views/Profile.vue'
import Users from '../views/Users.vue'
import Notes from '../views/Notes.vue'
import NoteDetail from '../views/NoteDetail.vue'
import CreateNote from '../views/CreateNote.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
	{
		path: '/',
		redirect: '/login'
	},
	{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/register',
		name: 'Register',
		component: Register
	},
	{
		path: '/dashboard',
		name: 'Dashboard',
		component: Dashboard,
		meta: { requiresAuth: true }
	},
	{
		path: '/profile',
		name: 'Profile',
		component: Profile,
		meta: { requiresAuth: true }
	},
	{
		path: '/users',
		name: 'Users',
		component: Users,
		meta: { requiresAuth: true }
	},
	{
		path: '/notes',
		name: 'Notes',
		component: Notes,
		meta: { requiresAuth: true }
	},
	{
		path: '/notes/new',
		name: 'CreateNote',
		component: CreateNote,
		meta: { requiresAuth: true }
	},
	{
		path: '/notes/:id',
		name: 'NoteDetail',
		component: NoteDetail,
		meta: { requiresAuth: true }
	},
	{
		path: '/user/:userId/:username',
		name: 'UserProfile',
		component: UserProfile,
		meta: { requiresAuth: true }
	}
]

const router = createRouter({
	history: createWebHistory(process.env.BASE_URL),
	routes
})

router.beforeEach((to, from, next) => {
	const isAuthenticated = localStorage.getItem('token')
	
	if (to.meta.requiresAuth && !isAuthenticated) {
		next('/login')
	} else if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
		next('/dashboard')
	} else {
		next()
	}
})

export default router

