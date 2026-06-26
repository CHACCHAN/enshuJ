<template>
  <div class="modal d-block" tabindex="-1" @click.self="$emit('close')">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">新規ダイレクトメッセージ</h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body">
          <input
            v-model="query"
            type="text"
            class="form-control mb-3"
            placeholder="ユーザー名で検索..."
            @input="search"
          />
          <div v-if="results.length === 0 && query" class="text-muted text-center">
            見つかりませんでした
          </div>
          <div
            v-for="u in results"
            :key="u.userId"
            class="d-flex align-items-center p-2 rounded cursor-pointer user-select-none"
            style="cursor:pointer"
            :class="selected?.userId === u.userId ? 'bg-primary text-white' : 'hover-bg'"
            @click="selected = u"
          >
            <img v-if="u.avatarPath" :src="`/api/avatars/${u.avatarPath}`"
                 class="rounded-circle me-2" width="32" height="32" style="object-fit:cover" />
            <div v-else class="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center me-2"
                 style="width:32px;height:32px;font-size:0.8rem">
              {{ (u.displayName ?? u.username)[0].toUpperCase() }}
            </div>
            <div>
              <div class="fw-semibold">{{ u.displayName ?? u.username }}</div>
              <div class="small opacity-75">@{{ u.username }}</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="$emit('close')">キャンセル</button>
          <button class="btn btn-primary" :disabled="!selected" @click="create">DMを開く</button>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-backdrop fade show"></div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { chatApi } from '../../api/chat'
import type { UserProfile, ChatRoom } from '../../types/chat'

const emit = defineEmits<{ close: []; created: [room: ChatRoom] }>()
const router = useRouter()

const query = ref('')
const results = ref<UserProfile[]>([])
const selected = ref<UserProfile | null>(null)

let debounceTimer: ReturnType<typeof setTimeout>
function search() {
  clearTimeout(debounceTimer)
  if (!query.value.trim()) { results.value = []; return }
  debounceTimer = setTimeout(async () => {
    results.value = await chatApi.searchUsers(query.value)
  }, 300)
}

async function create() {
  if (!selected.value) return
  const room = await chatApi.createRoom({ type: 'DM', memberIds: [selected.value.userId] })
  emit('created', room)
  router.push(`/chat/${room.id}`)
}
</script>
