<template>
  <div class="d-flex flex-column h-100">
    <!-- ヘッダー -->
    <div class="p-3 border-bottom bg-white d-flex align-items-center">
      <div class="fw-bold fs-5">{{ roomTitle }}</div>
      <span v-if="room?.type === 'GROUP'" class="badge bg-secondary ms-2">グループ</span>
    </div>

    <!-- メッセージ一覧 -->
    <div class="flex-grow-1 overflow-auto p-3" ref="scrollEl">
      <div v-if="loading" class="text-center text-muted py-4">読み込み中...</div>
      <div v-for="msg in messages" :key="msg.id"
           class="d-flex mb-3"
           :class="isOwn(msg) ? 'justify-content-end' : 'justify-content-start'">
        <!-- 相手のアバター -->
        <div v-if="!isOwn(msg)" class="me-2 flex-shrink-0">
          <img v-if="msg.senderAvatarPath"
               :src="`/api/avatars/${msg.senderAvatarPath}`"
               class="rounded-circle" width="36" height="36" style="object-fit:cover" />
          <div v-else class="rounded-circle bg-secondary d-flex align-items-center justify-content-center text-white"
               style="width:36px;height:36px;font-size:0.85rem">
            {{ (msg.senderDisplayName ?? '?')[0].toUpperCase() }}
          </div>
        </div>

        <div :class="isOwn(msg) ? 'text-end' : ''">
          <div v-if="!isOwn(msg)" class="small text-muted mb-1">{{ msg.senderDisplayName }}</div>
          <div class="d-inline-block px-3 py-2 rounded-3"
               :class="isOwn(msg) ? 'bg-primary text-white' : 'bg-light text-dark'"
               style="max-width: 480px; word-break: break-word; white-space: pre-wrap;">
            {{ msg.content }}
          </div>
          <div class="small text-muted mt-1">{{ formatTime(msg.sentAt) }}</div>
        </div>
      </div>
    </div>

    <!-- 入力エリア -->
    <div class="p-3 border-top bg-white">
      <div class="d-flex gap-2">
        <textarea
          v-model="inputText"
          class="form-control"
          rows="2"
          placeholder="メッセージを入力... (Enter で送信、Shift+Enter で改行)"
          @keydown.enter.exact.prevent="sendMessage"
        ></textarea>
        <button class="btn btn-primary px-4" @click="sendMessage" :disabled="!inputText.trim()">
          送信
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import { useStomp } from '../../composables/useStomp'
import { chatApi } from '../../api/chat'
import type { ChatRoom, Message } from '../../types/chat'

const props = defineProps<{ currentUser: { userId: number } | null }>()
const emit = defineEmits<{ messageSent: [] }>()

const route = useRoute()
const { currentUser } = useAuth()
const { subscribe, unsubscribe, send } = useStomp()

const roomId = computed(() => Number(route.params.roomId))
const room = ref<ChatRoom | null>(null)
const messages = ref<Message[]>([])
const inputText = ref('')
const loading = ref(false)
const scrollEl = ref<HTMLElement | null>(null)
const seenIds = new Set<number>()

const roomTitle = computed(() => {
  if (!room.value) return ''
  if (room.value.type === 'GROUP') return room.value.name ?? 'グループ'
  const other = room.value.members.find(m => m.userId !== currentUser.value?.userId)
  return other?.displayName ?? other?.username ?? 'DM'
})

function isOwn(msg: Message) {
  return msg.senderId === currentUser.value?.userId
}

function formatTime(iso: string) {
  return new Date(iso).toLocaleTimeString('ja-JP', { hour: '2-digit', minute: '2-digit' })
}

function addMessage(msg: Message) {
  if (seenIds.has(msg.id)) return
  seenIds.add(msg.id)
  messages.value.push(msg)
  nextTick(scrollToBottom)
}

function scrollToBottom() {
  if (scrollEl.value) scrollEl.value.scrollTop = scrollEl.value.scrollHeight
}

async function loadRoom() {
  loading.value = true
  seenIds.clear()
  messages.value = []
  try {
    const msgs = await chatApi.getMessages(roomId.value)
    msgs.forEach(m => { seenIds.add(m.id); })
    messages.value = msgs
    const allRooms = await chatApi.getRooms()
    room.value = allRooms.find(r => r.id === roomId.value) ?? null
    nextTick(scrollToBottom)
  } finally {
    loading.value = false
  }
}

function sendMessage() {
  const content = inputText.value.trim()
  if (!content) return
  send('/app/chat.send', { roomId: roomId.value, content })
  inputText.value = ''
  emit('messageSent')
}

watch(roomId, async (newId, oldId) => {
  if (oldId) unsubscribe(`/topic/room/${oldId}`)
  await loadRoom()
  subscribe(`/topic/room/${newId}`, (msg) => addMessage(msg as Message))
})

onMounted(async () => {
  await loadRoom()
  subscribe(`/topic/room/${roomId.value}`, (msg) => addMessage(msg as Message))
})

onUnmounted(() => {
  unsubscribe(`/topic/room/${roomId.value}`)
})
</script>
