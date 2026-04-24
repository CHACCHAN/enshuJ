<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'

const username = ref('')
const password = ref('')
const error = ref('')

const handleLogin = async () => {
    const params = new URLSearchParams()
    params.append('username', username.value)
    params.append('password', password.value)

    try {
        await axios.post('/api/login', params)
        window.location.href = '/';
    } catch (err) {
        error.value = 'ユーザー名またはパスワードが違います'
    }
}
</script>

<template>
    <div class="container mt-5" style="max-width: 400px;">
        <div class="card p-4 shadow">
            <h2 class="text-center mb-4">Login</h2>
            <div class="mb-3">
                <label class="form-label">Username</label>
                <input v-model="username" class="form-control" type="text">
            </div>
            <div class="mb-3">
                <label class="form-label">Password</label>
                <input v-model="password" class="form-control" type="password">
            </div>
            <button @click="handleLogin" class="btn btn-primary w-100">Sign In</button>
            <p v-if="error" class="mt-3 text-danger text-center">{{ error }}</p>
            <div class="mt-3 text-center">
                <router-link to="/auth/register">新規登録はこちら</router-link>
            </div>
        </div>
    </div>
</template>