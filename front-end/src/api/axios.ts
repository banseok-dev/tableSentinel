import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://192.168.0.11:8080', 
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