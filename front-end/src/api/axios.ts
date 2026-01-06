import axios from 'axios'

const instance = axios.create({
  // for nginx proxy
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 요청 인터셉터 (로그 찍기용)
instance.interceptors.request.use(
  (config) => {
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, config.data)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default instance