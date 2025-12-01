<script setup>
import { ref } from 'vue'
import axios from 'axios'

const targetIp = ref('1.1.1.1')
const commandType = ref('ADD_IP')
const logs = ref([])

// [핵심] 백엔드로 명령 발사
const sendCommand = async () => {
  try {
    // API 규격(DTO)에 맞춰 데이터 생성
    const payload = {
      type: commandType.value,
      ipAddress: targetIp.value
    }

    // POST 요청 (백엔드 주소 확인 필수)
    const response = await axios.post('http://localhost:8080/api/agents/node1/xdp/commands', payload)
    
    logs.value.push(`성공: ${response.data}`)
    console.log('백엔드 응답:', response)

  } catch (error) {
    logs.value.push(`실패: ${error.message}`)
    console.error('에러:', error)
  }
}
</script>

<template>
  <div>
    <h1>🛡️ tableSentinel Commander</h1>
    
    <div>
      <select v-model="commandType">
        <option value="ADD_IP">IP 차단 (ADD)</option>
        <option value="DEL_IP">차단 해제 (DEL)</option>
      </select>
      <input v-model="targetIp" placeholder="IP 주소 입력" />
      <button @click="sendCommand">명령 전송</button>
    </div>

    <div style="margin-top: 20px; border: 1px solid #ccc; padding: 10px;">
      <h3>Logs:</h3>
      <ul>
        <li v-for="(log, index) in logs" :key="index">{{ log }}</li>
      </ul>
    </div>
  </div>
</template>