<script setup>
import { ref } from 'vue'
import axios from 'axios'

const engineType = ref('XDP')
const targetIp = ref('1.1.1.1')
const commandType = ref('ADD_IP')
const logs = ref([])
const agentsId = ref('node1')

const sendCommand = async () => {
  try {
    let url = ''
    let payload = {}

    if (engineType.value === 'XDP') {
      // XDP
      url = `http://localhost:8080/api/agents${agentsId.value}/xdp/commands`
      
      payload = {
        engineType: 'XDP',
        commandType: commandType.value,
        ipAddress: targetIp.value, 
        timestamp: Date.now()
      }
    } else {
      // NFT
      url = `http://localhost:8080/api/agents/${agentsId.value}/nft/commands`
      
      payload = {
        engineType: 'nftables',
        commandType: commandType.value,
        targetIp: targetIp.value,
        table: 'filter',
        chain: 'input',
        action: 'drop',
        timestamp: Date.now()
      }
    }

    const response = await axios.post(url, payload)
    
    logs.value.push(`[${engineType.value}] 성공: ${response.data}`)
    console.log('백엔드 응답:', response)

  } catch (error) {
    logs.value.push(`실패: ${error.message}`)
    console.error('에러:', error)
  }
}
</script>

<template>
  <div>
    <h1>tableSentinel Commander</h1>
    
    <div>
      <select v-model="engineType">
        <option value="XDP">XDP</option>
        <option value="nftables">nftables</option>
      </select>
      <select v-model="commandType">
        <option value="ADD_IP">IP 차단 (ADD), nft는 IP Accept</option> // 임시 처리
        <option value="DEL_IP">차단 해제 (DEL), nft는 IP Drop</option> // 임시 처리
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