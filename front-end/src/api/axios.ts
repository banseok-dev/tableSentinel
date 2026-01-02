// src/api/axios.ts
import axios from 'axios'

// Spring Boot 서버 주소 (CORS 설정해둔 8080)
const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 5000, // 5초 안에 응답 없으면 에러
})

// 요청 인터셉터 (로그 찍기용)
instance.interceptors.request.use(
  (config) => {
    console.log(`🚀 [API Request] ${config.method?.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default instance