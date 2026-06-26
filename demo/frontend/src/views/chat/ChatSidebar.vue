<template>
  <div class="d-flex flex-column border-end bg-white" style="width: 280px; min-width: 280px;">
    <!-- ヘッダー -->
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
      <span class="fw-bold fs-5">チャット</span>
      <div class="d-flex gap-1">
        <RouterLink to="/" class="btn btn-sm btn-outline-secondary">
          <i class="bi bi-house"></i> 戻る
        </RouterLink>
        <button class="btn btn-sm btn-outline-secondary" title="新規DM" @click="showDmModal = true">
          <i class="bi bi-person-plus"></i> DM
        </button>
        <button class="btn btn-sm btn-outline-secondary" title="グループ作成" @click="showGroupModal = true">
          <i class="bi bi-people"></i>
        </button>
      </div>
    </div>

    <!-- ルーム一覧 -->
    <div class="overflow-auto flex-grow-1">
      <RouterLink
        v-for="room in rooms"
        :key="room.id"
        :to="`/chat/${room.id}`"
        class="text-decoration-none"
      >
        <div
          class="d-flex align-items-center px-3 py-2 border-bottom room-item"
          :class="{ 'bg-primary bg-opacity-10': activeRoomId === room.id }"
        >
          <!-- アバター -->
          <div class="me-2 flex-shrink-0 position-relative">
            <img
              v-if="roomAvatar(room)"
              :src="`/api/avatars/${roomAvatar(room)}`"
              class="rounded-circle"
              width="40" height="40"
              style="object-fit:cover"
            />
            <div v-else class="rounded-circle bg-secondary d-flex align-items-center justify-content-center text-white"
                 style="width:40px;height:40px;font-size:1.1rem">
              {{ roomInitial(room) }}
            </div>
            <!-- 未読バッジ -->
            <span
              v-if="unreadCounts[room.id]"
              class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
              style="font-size:0.65rem"
            >
              {{ unreadCounts[room.id] > 99 ? '99+' : unreadCounts[room.id] }}
            </span>
          </div>

          <div class="flex-grow-1 overflow-hidden">
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-semibold text-dark text-truncate">{{ roomDisplayName(room) }}</span>
            </div>
            <div class="text-muted small text-truncate"
                 :class="{ 'fw-semibold text-dark': unreadCounts[room.id] }">
              {{ room.lastMessage?.content ?? '(メッセージなし)' }}
            </div>
          </div>
        </div>
      </RouterLink>

      <div v-if="rooms.length === 0" class="text-muted text-center p-4 small">
        まだ会話がありません
      </div>
    </div>

    <!-- フッター: プロフィール/設定 -->
    <div class="border-top p-2 d-flex align-items-center gap-2">
      <img
        v-if="currentUser?.avatarPath"
        :src="`/api/avatars/${currentUser.avatarPath}`"
        class="rounded-circle"
        width="32" height="32"
        style="object-fit:cover"
      />
      <div v-else class="rounded-circle bg-primary d-flex align-items-center justify-content-center text-white"
           style="width:32px;height:32px;font-size:0.85rem">
        {{ (currentUser?.displayName ?? currentUser?.username ?? '?')[0].toUpperCase() }}
      </div>
      <span class="text-truncate flex-grow-1 small fw-semibold">
        {{ currentUser?.displayName ?? currentUser?.username }}
      </span>
      <RouterLink to="/profile" class="btn btn-sm btn-link p-0" title="プロフィール">
        <i class="bi bi-person-circle"></i>
      </RouterLink>
      <RouterLink to="/settings" class="btn btn-sm btn-link p-0" title="設定">
        <i class="bi bi-gear"></i>
      </RouterLink>
    </div>

    <!-- 新規DM モーダル -->
    <NewDmModal v-if="showDmModal" @close="showDmModal = false" @created="onCreated" />
    <!-- グループ作成モーダル -->
    <CreateGroupModal v-if="showGroupModal" @close="showGroupModal = false" @created="onCreated" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ChatRoom, UserProfile } from '../../types/chat'
import NewDmModal from './NewDmModal.vue'
import CreateGroupModal from './CreateGroupModal.vue'

const props = defineProps<{
  rooms: ChatRoom[]
  currentUser: UserProfile | null
  activeRoomId: number | null
  unreadCounts: Record<number, number>
}>()

const emit = defineEmits<{
  roomCreated: [room: ChatRoom]
}>()

const showDmModal = ref(false)
const showGroupModal = ref(false)

function roomDisplayName(room: ChatRoom) {
  if (room.type === 'GROUP') return room.name ?? 'グループ'
  const other = room.members.find(m => m.userId !== props.currentUser?.userId)
  return other?.displayName ?? other?.username ?? 'DM'
}

function roomAvatar(room: ChatRoom) {
  if (room.type === 'GROUP') return null
  const other = room.members.find(m => m.userId !== props.currentUser?.userId)
  return other?.avatarPath ?? null
}

function roomInitial(room: ChatRoom) {
  return roomDisplayName(room)[0]?.toUpperCase() ?? '?'
}

function onCreated(room: ChatRoom) {
  showDmModal.value = false
  showGroupModal.value = false
  emit('roomCreated', room)
}
</script>

<style scoped>
.room-item:hover { background-color: #f8f9fa; }
</style>
