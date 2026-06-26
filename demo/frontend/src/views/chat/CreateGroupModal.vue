<template>
  <div class="modal d-block" tabindex="-1" @click.self="$emit('close')">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">グループ作成</h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label fw-semibold">グループ名</label>
            <input v-model="groupName" type="text" class="form-control" placeholder="グループ名を入力" />
          </div>
          <div class="mb-2">
            <label class="form-label fw-semibold">メンバーを追加</label>
            <input
              v-model="query"
              type="text"
              class="form-control mb-2"
              placeholder="ユーザー名で検索..."
              @input="search"
            />
            <div v-for="u in results" :key="u.userId"
                 class="d-flex align-items-center p-2 rounded mb-1"
                 style="cursor:pointer"
                 :class="isSelected(u) ? 'bg-primary bg-opacity-10 border border-primary' : 'bg-light'"
                 @click="toggleMember(u)">
              <input type="checkbox" class="form-check-input me-2" :checked="isSelected(u)" @click.stop />
              <span class="fw-semibold">{{ u.displayName ?? u.username }}</span>
              <span class="small text-muted ms-1">@{{ u.username }}</span>
            </div>
          </div>
          <div v-if="selectedMembers.length > 0" class="d-flex flex-wrap gap-1 mt-2">
            <span
              v-for="m in selectedMembers" :key="m.userId"
              class="badge bg-primary d-flex align-items-center gap-1"
            >
              {{ m.displayName ?? m.username }}
              <button class="btn-close btn-close-white" style="font-size:0.6rem" @click="toggleMember(m)"></button>
            </span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="$emit('close')">キャンセル</button>
          <button class="btn btn-primary"
                  :disabled="!groupName.trim() || selectedMembers.length === 0"
                  @click="create">
            作成
          </button>
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

const groupName = ref('')
const query = ref('')
const results = ref<UserProfile[]>([])
const selectedMembers = ref<UserProfile[]>([])

let debounceTimer: ReturnType<typeof setTimeout>
function search() {
  clearTimeout(debounceTimer)
  if (!query.value.trim()) { results.value = []; return }
  debounceTimer = setTimeout(async () => {
    results.value = await chatApi.searchUsers(query.value)
  }, 300)
}

function isSelected(u: UserProfile) {
  return selectedMembers.value.some(m => m.userId === u.userId)
}

function toggleMember(u: UserProfile) {
  if (isSelected(u)) {
    selectedMembers.value = selectedMembers.value.filter(m => m.userId !== u.userId)
  } else {
    selectedMembers.value.push(u)
  }
}

async function create() {
  if (!groupName.value.trim() || selectedMembers.value.length === 0) return
  const room = await chatApi.createRoom({
    type: 'GROUP',
    name: groupName.value.trim(),
    memberIds: selectedMembers.value.map(m => m.userId),
  })
  emit('created', room)
  router.push(`/chat/${room.id}`)
}
</script>
