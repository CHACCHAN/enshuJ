<template>
  <div class="d-flex vh-100 overflow-hidden">
    <ChatSidebar
      :rooms="rooms"
      :current-user="currentUser"
      :active-room-id="activeRoomId"
      :unread-counts="unreadCounts"
      @room-created="onRoomCreated"
    />
    <div class="flex-grow-1 d-flex flex-column overflow-hidden">
      <RouterView v-slot="{ Component }">
        <component
          :is="Component"
          :current-user="currentUser"
          @message-sent="refreshRooms"
        />
      </RouterView>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import { useStomp } from '../../composables/useStomp'
import { chatApi } from '../../api/chat'
import type { ChatRoom, Message } from '../../types/chat'
import ChatSidebar from './ChatSidebar.vue'

const { currentUser } = useAuth()
const { connect, subscribe } = useStomp()
const route = useRoute()

const rooms = ref<ChatRoom[]>([])
const unreadCounts = ref<Record<number, number>>({})

const activeRoomId = computed(() => {
  const id = route.params.roomId
  return id ? Number(id) : null
})

// ルームを開いたら未読カウントをクリア
watch(activeRoomId, (newId) => {
  if (newId && unreadCounts.value[newId]) {
    const next = { ...unreadCounts.value }
    delete next[newId]
    unreadCounts.value = next
  }
})

async function fetchRooms() {
  rooms.value = await chatApi.getRooms()
}

function refreshRooms() {
  fetchRooms()
}

function onRoomCreated(room: ChatRoom) {
  if (!rooms.value.find(r => r.id === room.id)) {
    rooms.value = [room, ...rooms.value]
  }
}

function bumpUnread(roomId: number) {
  // アクティブなルームは未読扱いしない
  if (roomId === activeRoomId.value) return
  unreadCounts.value = {
    ...unreadCounts.value,
    [roomId]: (unreadCounts.value[roomId] ?? 0) + 1,
  }
}

onMounted(async () => {
  await fetchRooms()
  connect(() => {
    // 相手から来た DM 通知 (送信者が /topic/room/X に届けた後、相手に追加送信される)
    subscribe('/user/queue/messages', async (msg) => {
      const message = msg as Message
      const existing = rooms.value.find(r => r.id === message.roomId)

      if (existing) {
        // 既知のルーム → lastMessage を更新してサイドバーの先頭へ
        existing.lastMessage = message
        rooms.value = [existing, ...rooms.value.filter(r => r.id !== existing.id)]
      } else {
        // 未知のルーム (初めてメッセージが来た) → ルーム一覧を再取得して追加
        await fetchRooms()
      }

      bumpUnread(message.roomId)
    })
  })
})
</script>
