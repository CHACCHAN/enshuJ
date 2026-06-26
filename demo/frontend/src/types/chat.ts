export interface UserProfile {
  userId: number
  username: string
  displayName: string | null
  bio: string | null
  avatarPath: string | null
}

export interface Message {
  id: number
  roomId: number
  senderId: number | null
  senderDisplayName: string | null
  senderAvatarPath: string | null
  content: string
  sentAt: string
}

export interface ChatRoom {
  id: number
  name: string | null
  type: 'DM' | 'GROUP'
  createdAt: string
  members: UserProfile[]
  lastMessage: Message | null
}
