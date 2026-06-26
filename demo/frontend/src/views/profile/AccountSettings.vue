<template>
  <div class="container py-4" style="max-width: 500px;">
    <div class="d-flex align-items-center mb-4">
      <button class="btn btn-outline-secondary btn-sm me-3" @click="router.back()">← 戻る</button>
      <h2 class="mb-0">アカウント設定</h2>
    </div>

    <div class="card">
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">現在のユーザー名</label>
          <input type="text" class="form-control" :value="currentUsername" disabled />
        </div>

        <hr />
        <h6 class="mb-3">情報を変更する</h6>

        <div class="mb-3">
          <label class="form-label">新しいユーザー名 <span class="text-muted small">（変更しない場合は空欄）</span></label>
          <input v-model="form.newUsername" type="text" class="form-control" placeholder="新しいユーザー名" />
        </div>

        <div class="mb-3">
          <label class="form-label">新しいパスワード <span class="text-muted small">（変更しない場合は空欄）</span></label>
          <input v-model="form.newPassword" type="password" class="form-control" placeholder="新しいパスワード" />
        </div>

        <div class="mb-3">
          <label class="form-label fw-semibold">現在のパスワード <span class="text-danger">*</span></label>
          <input v-model="form.currentPassword" type="password" class="form-control" placeholder="現在のパスワード（必須）" />
        </div>

        <div v-if="success" class="alert alert-success py-2">
          更新しました。{{ usernameChanged ? '再ログインが必要な場合があります。' : '' }}
        </div>
        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

        <button class="btn btn-primary w-100" @click="save" :disabled="saving || !form.currentPassword">
          {{ saving ? '更新中...' : '変更を保存' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { profileApi } from '../../api/chat'
import { useRouter } from 'vue-router'

const currentUsername = ref('')
const usernameChanged = ref(false)
const form = ref({ newUsername: '', newPassword: '', currentPassword: '' })
const saving = ref(false)
const success = ref(false)
const error = ref('')

const router = useRouter();

onMounted(async () => {
  const account = await profileApi.getAccount()
  currentUsername.value = account.username
})

async function save() {
  if (!form.value.currentPassword) return
  saving.value = true
  success.value = false
  error.value = ''
  usernameChanged.value = false
  try {
    await profileApi.updateAccount({
      newUsername: form.value.newUsername || undefined,
      currentPassword: form.value.currentPassword,
      newPassword: form.value.newPassword || undefined,
    })
    if (form.value.newUsername && form.value.newUsername !== currentUsername.value) {
      currentUsername.value = form.value.newUsername
      usernameChanged.value = true
    }
    form.value = { newUsername: '', newPassword: '', currentPassword: '' }
    success.value = true
    setTimeout(() => { success.value = false }, 4000)
  } catch {
    error.value = '更新に失敗しました。現在のパスワードを確認してください。'
  } finally {
    saving.value = false
  }
}
</script>
