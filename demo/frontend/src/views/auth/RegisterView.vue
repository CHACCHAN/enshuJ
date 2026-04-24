<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({ username: '', password: '', nickname: '' })
const message = ref('')

const handleRegister = async () => {
    try {
        const res = await axios.post('/api/register', form.value)
        message.value = res.data
        if (res.data.includes('Success')) {
            router.push({ name: 'login'})
        }
    } catch (err) {
        message.value = 'エラーが発生しました'
    }
}
</script>

<template>
    <div class="container mt-5" style="max-width: 400px;">
        <div class="card p-4 shadow">
            <h2 class="text-center mb-4">Register</h2>
            <div class="mb-3">
                <input v-model="form.username" class="form-control" placeholder="Username">
            </div>
            <div class="mb-3">
                <input v-model="form.password" type="password" class="form-control" placeholder="Password">
            </div>
            <div class="mb-3">
                <input v-model="form.nickname" class="form-control" placeholder="Nickname">
            </div>
            <button @click="handleRegister" class="btn btn-primary w-100">Register</button>
            <p class="mt-3 text-center text-danger">{{ message }}</p>
            <div class="mt-3 text-center">
                <router-link to="/auth/login">ログインはこちら</router-link>
            </div>
        </div>
    </div>
</template>