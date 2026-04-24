<script setup lang="ts">
import axios from 'axios'

const messages = [
    { name: 'ユーザー1', message: 'こんにちは！' },
    { name: 'ユーザー2', message: '元気ですか？' },
]
const handleLogout = async () => {
    try {
        await axios.post('/api/logout')
        window.location.href = '/auth/login'
    } catch (err) {
        console.error('Logout failed', err)
    }
}
</script>

<template>
    <div class="container mt-5 text-center">
        <div class="jumbotron shadow p-5 bg-light">
            <h1 class="display-4">ようこそ</h1>
            <p class="lead">Demoサイトです</p>
            <hr class="my-4">

            <div v-for="msg in messages" :key="msg.name">
                <p><strong>{{ msg.name }}</strong>: {{ msg.message }}</p>
            </div>
            
            <button @click="handleLogout" class="btn btn-outline-danger">
                ログアウト
            </button>
        </div>
    </div>
</template>