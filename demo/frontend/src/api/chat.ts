import api from '../lib/api'
import type { ChatRoom, Message, UserProfile } from '../types/chat'

export const chatApi = {
  getRooms: () =>
    api.get<ChatRoom[]>('/chat/rooms').then(r => r.data),

  createRoom: (req: { type: string; name?: string; memberIds: number[] }) =>
    api.post<ChatRoom>('/chat/rooms', req).then(r => r.data),

  getMessages: (roomId: number, page = 0) =>
    api.get<Message[]>(`/chat/rooms/${roomId}/messages`, { params: { page } }).then(r => r.data),

  searchUsers: (q: string) =>
    api.get<UserProfile[]>('/users/search', { params: { q } }).then(r => r.data),
}

export const profileApi = {
  getProfile: () =>
    api.get<UserProfile>('/profile').then(r => r.data),

  updateProfile: (data: { displayName?: string; bio?: string }) =>
    api.put<UserProfile>('/profile', data).then(r => r.data),

  uploadAvatar: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return api.post<{ avatarPath: string }>('/profile/avatar', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then(r => r.data)
  },

  getAccount: () =>
    api.get<{ username: string }>('/account').then(r => r.data),

  updateAccount: (data: { newUsername?: string; currentPassword: string; newPassword?: string }) =>
    api.put('/account', data),
}
