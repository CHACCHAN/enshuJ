import axios from "axios"
import { useRouter } from "vue-router"

const router = useRouter();

const api = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      // 未認証ならログインページへ
      router.push("/auth/login")
    }
    return Promise.reject(err)
  }
)

export default api