<script setup lang="ts">
import { useRouter } from "vue-router"
import axios from "axios"

const router = useRouter()

const messages = [
    { name: "ユーザー1", message: "こんにちは！" },
    { name: "ユーザー2", message: "元気ですか？" },
]

const navItems = [
    { label: "商品管理", desc: "在庫・価格の管理", to: "/shop" },
]

function getInitials(name: string): string {
    return name.slice(0, 2)
}

const handleLogout = async (): Promise<void> => {
    try {
        await axios.post("/api/logout")
        router.push("/auth/login")
    } catch (err) {
        console.error("Logout failed", err)
    }
}
</script>

<template>
    <div class="min-vh-100 bg-light py-4">
        <div class="container" style="max-width: 720px;">
            <div class="card border rounded-3 overflow-hidden">

                <!-- ナビバー -->
                <div class="d-flex align-items-center justify-content-between px-4 border-bottom" style="height: 52px;">
                    <span class="fw-medium">Demo</span>
                    <button class="btn btn-sm btn-outline-danger" @click="handleLogout">
                        ログアウト
                    </button>
                </div>

                <div class="card-body px-4 pb-4">

                    <!-- ヒーロー -->
                    <div class="text-center py-4">
                        <p class="text-muted small mb-1" style="letter-spacing: 0.08em;">DEMO SITE</p>
                            <h1 class="h4 fw-medium mb-2">ようこそ</h1>
                        <p class="text-muted small mb-0">管理機能へのアクセスはこちらから</p>
                    </div>

                    <!-- ナビカード -->
                    <div class="row g-3 mb-4">
                        <div v-for="item in navItems" :key="item.to" class="col-6 col-md-4">
                            <RouterLink :to="item.to" class="card h-100 text-decoration-none text-body border rounded-3 p-3 d-flex flex-column gap-2">
                                <div class="fw-medium small">{{ item.label }}</div>
                                <div class="text-muted" style="font-size: 12px;">{{ item.desc }}</div>
                            </RouterLink>
                        </div>
                    </div>

                    <hr class="mb-4">

                    <!-- メッセージ -->
                    <div class="card border rounded-3 overflow-hidden">
                        <div class="px-3 py-2 border-bottom bg-light">
                            <span class="text-muted small fw-medium">メッセージ</span>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li v-for="msg in messages" :key="msg.name" class="list-group-item d-flex align-items-start gap-3 py-3">
                                <div
                                    class="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0 bg-primary bg-opacity-10 text-primary fw-medium"
                                    style="width: 32px; height: 32px; font-size: 11px;"
                                >
                                    {{ getInitials(msg.name) }}
                                </div>
                                <div>
                                    <div class="small fw-medium">{{ msg.name }}</div>
                                    <div class="small text-muted">{{ msg.message }}</div>
                                </div>
                            </li>
                        </ul>
                    </div>

                </div>
            </div>
        </div>
    </div>
</template>