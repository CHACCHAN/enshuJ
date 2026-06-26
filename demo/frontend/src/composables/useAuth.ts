import { ref } from 'vue'
import { profileApi } from '../api/chat'
import type { UserProfile } from '../types/chat'

const currentUser = ref<UserProfile | null>(null)
const authChecked = ref(false)

export function useAuth() {
  async function fetchCurrentUser() {
    try {
      currentUser.value = await profileApi.getProfile()
    } catch {
      currentUser.value = null
    } finally {
      authChecked.value = true
    }
  }

  return { currentUser, authChecked, fetchCurrentUser }
}
