<template>
  <div class="container py-4" style="max-width: 600px;">
    <div class="d-flex align-items-center mb-4">
      <RouterLink to="/chat" class="btn btn-outline-secondary btn-sm me-3">← チャットに戻る</RouterLink>
      <h2 class="mb-0">プロフィール編集</h2>
    </div>

    <div v-if="loading" class="text-center py-5 text-muted">読み込み中...</div>

    <template v-else>
      <!-- アバター -->
      <div class="card mb-4">
        <div class="card-body">
          <h5 class="card-title">アイコン</h5>
          <div class="d-flex align-items-center gap-3 mb-3">
            <img v-if="profile?.avatarPath"
                 :src="`/api/avatars/${profile.avatarPath}?t=${avatarTs}`"
                 class="rounded-circle border"
                 width="80" height="80" style="object-fit:cover" />
            <div v-else class="rounded-circle bg-primary d-flex align-items-center justify-content-center text-white"
                 style="width:80px;height:80px;font-size:2rem">
              {{ (profile?.displayName ?? profile?.username ?? '?')[0].toUpperCase() }}
            </div>
            <div>
              <p class="text-muted small mb-2">アイコン画像をアップロード（正方形推奨）</p>
              <button class="btn btn-sm btn-outline-primary" @click="showCropper = !showCropper">
                {{ showCropper ? '閉じる' : 'アイコンを変更' }}
              </button>
            </div>
          </div>
          <div v-if="showCropper">
            <ImageCropper :aspect-ratio="1" :preview-size="80" @cropped="onAvatarCropped" />
            <button v-if="pendingAvatarFile" class="btn btn-primary mt-2" @click="uploadAvatar" :disabled="uploadingAvatar">
              {{ uploadingAvatar ? 'アップロード中...' : 'アップロード' }}
            </button>
          </div>
          <div v-if="avatarSuccess" class="alert alert-success py-2 mt-2">アイコンを更新しました</div>
        </div>
      </div>

      <!-- プロフィール情報 -->
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">基本情報</h5>
          <div class="mb-3">
            <label class="form-label">表示名</label>
            <input v-model="form.displayName" type="text" class="form-control" placeholder="表示名" />
          </div>
          <div class="mb-3">
            <label class="form-label">自己紹介</label>
            <textarea v-model="form.bio" class="form-control" rows="4" placeholder="自己紹介文"></textarea>
          </div>
          <div v-if="saveSuccess" class="alert alert-success py-2">保存しました</div>
          <div v-if="saveError" class="alert alert-danger py-2">{{ saveError }}</div>
          <button class="btn btn-primary" @click="save" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { profileApi } from '../../api/chat'
import { useAuth } from '../../composables/useAuth'
import ImageCropper from '../../components/ImageCropper.vue'
import type { UserProfile } from '../../types/chat'

const { currentUser, fetchCurrentUser } = useAuth()

const loading = ref(true)
const profile = ref<UserProfile | null>(null)
const form = ref({ displayName: '', bio: '' })
const saving = ref(false)
const saveSuccess = ref(false)
const saveError = ref('')
const showCropper = ref(false)
const pendingAvatarFile = ref<File | null>(null)
const uploadingAvatar = ref(false)
const avatarSuccess = ref(false)
const avatarTs = ref(Date.now())

onMounted(async () => {
  profile.value = await profileApi.getProfile()
  form.value.displayName = profile.value.displayName ?? ''
  form.value.bio = profile.value.bio ?? ''
  loading.value = false
})

function onAvatarCropped(file: File) {
  pendingAvatarFile.value = file
}

async function uploadAvatar() {
  if (!pendingAvatarFile.value) return
  uploadingAvatar.value = true
  avatarSuccess.value = false
  try {
    const res = await profileApi.uploadAvatar(pendingAvatarFile.value)
    if (profile.value) profile.value.avatarPath = res.avatarPath
    avatarTs.value = Date.now()
    avatarSuccess.value = true
    showCropper.value = false
    pendingAvatarFile.value = null
    await fetchCurrentUser()
  } finally {
    uploadingAvatar.value = false
  }
}

async function save() {
  saving.value = true
  saveSuccess.value = false
  saveError.value = ''
  try {
    const updated = await profileApi.updateProfile({
      displayName: form.value.displayName,
      bio: form.value.bio,
    })
    profile.value = updated
    saveSuccess.value = true
    await fetchCurrentUser()
    setTimeout(() => { saveSuccess.value = false }, 3000)
  } catch {
    saveError.value = '保存に失敗しました'
  } finally {
    saving.value = false
  }
}
</script>
