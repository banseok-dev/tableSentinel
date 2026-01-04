<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import Breadcrumb from '@/components/common/PageBreadcrumb.vue'
import Alert from '@/components/ui/Alert.vue'
import Button from '@/components/ui/Button.vue'
import api from '@/api/axios' // axios 인스턴스

// 상태 변수
const pageTitle = ref('Security Control Center')
const selectedAgent = ref<any>(null)
const loading = ref(false)
const targetIp = ref('')
const blockMode = ref('XDP') 

// 드롭다운 상태
const isDropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const dropdownItems = [
  { text: 'XDP (고속 패킷 처리)', value: 'XDP' },
  { text: 'NFTables (범용 패킷 처리)', value: 'NFTables' }
]

// 알림 메시지
const alertMessage = ref({ show: false, variant: 'success', title: '', message: '' })

const agents = ref<any[]>([])     // 백엔드에서 가져온 에이전트 목록
const agentRules = ref<any[]>([]) // 선택된 에이전트의 차단 규칙 목록 (임시 메모리)
let pollInterval: any = null      // 폴링용 인터벌 변수

// 백엔드 API 연동 함수
const fetchAgents = async () => {
  try {
    const response = await api.get('/api/v1/agents')
    
    agents.value = response.data.map((agent: any) => ({
        id: agent.uuid, 
        name: agent.hostname,
        ip: agent.ip_address, 
        status: agent.status || 'Offline',
        drop_rate: 'Monitoring'
    }))
  } catch (error) {
    console.error('Failed to fetch agents:', error)
  }
}

// API 차단 명령 전송
const sendBlockCommand = async () => {
  if (!targetIp.value || !selectedAgent.value) return
  
  loading.value = true
  alertMessage.value.show = false

  try {
    const uuid = selectedAgent.value.id
    
    // 모드에 따라 엔드포인트 및 페이로드 분기
    if (blockMode.value === 'XDP') {
      // XDP Command DTO 구조
      await api.post(`/api/v1/agents/${uuid}/xdp`, {
        action: 'ADD',
        targetIp: targetIp.value,
        mode: 'src,dst'
      })
    } else {
      // NFT Command DTO 구조
      await api.post(`/api/v1/agents/${uuid}/nft`, {
        action: 'ADD',
        chain: 'input',        // 기본 체인
        targetIp: targetIp.value,
        protocol: 'ip'         // 모든 프로토콜
      })
    }

    // agent 연결 성공 시 UI 업데이트
    agentRules.value.push({
      ip: targetIp.value,
      type: blockMode.value,
      interface: blockMode.value === 'XDP' ? 'eth0' : 'input',
      status: '🔗 Syncing'
    })

    alertMessage.value = {
      show: true,
      variant: 'success',
      title: 'Command Sent Successfully',
      message: `Blocked ${targetIp.value} via ${blockMode.value} on ${selectedAgent.value.name}`
    }
    targetIp.value = '' // 입력창 초기화

  } catch (e: any) {
    alertMessage.value = {
      show: true,
      variant: 'danger',
      title: 'Command Failed',
      message: e.response?.data?.message || 'Server connection failed.'
    }
  } finally {
    loading.value = false
  }
}

// API 차단 해제 (Unblock)
const handleUnblock = async (rule: any, index: number) => {
  if (!selectedAgent.value) return
  const confirmed = confirm(`Unblock ${rule.ip}?`)
  if (!confirmed) return

  try {
    const uuid = selectedAgent.value.id
    const payload = {
      action: 'DELETE',
      targetIp: rule.ip,
      // 기존에 저장된 정보 활용
      ...(rule.type === 'XDP' ? { interfaceName: rule.interface } : { chain: rule.interface }) 
    }
    
    const endpoint = rule.type === 'XDP' ? 'xdp' : 'nft'
    await api.post(`/api/v1/agents/${uuid}/${endpoint}`, payload)

    // UI에서 제거
    agentRules.value.splice(index, 1)

  } catch (e) {
    alert('Failed to unblock IP.')
  }
}

// UI 인터랙션 함수
const selectAgent = (agent: any) => {
  selectedAgent.value = agent
  alertMessage.value.show = false
  // 에이전트 변경 시 룰 리스트 초기화 (실제로는 API로 해당 에이전트의 룰을 불러와야 함)
  agentRules.value = [] 
}

const toggleDropdown = () => { isDropdownOpen.value = !isDropdownOpen.value }
const closeDropdown = () => { isDropdownOpen.value = false }
const handleItemClick = (item: any) => {
  blockMode.value = item.value
  closeDropdown()
}
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

// 라이프사이클
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  
  // 최초 데이터 로드
  fetchAgents()
  
  // 3초마다 에이전트 상태 갱신 (Heartbeat 반영을 위해)
  pollInterval = setInterval(fetchAgents, 3000)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (pollInterval) clearInterval(pollInterval)
})
</script>

<template>
  <AdminLayout>
    <Breadcrumb :pageTitle="pageTitle" />

    <div class="grid grid-cols-1 gap-9 xl:grid-cols-12">
      
      <div class="col-span-12 xl:col-span-5">
        <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
          <div class="border-b border-stroke py-4 px-6.5 dark:border-strokedark">
            <h3 class="font-medium text-black dark:text-white">Connected Agents</h3>
          </div>
          <div class="grid grid-cols-3 border-b border-stroke bg-gray-2 py-2 px-4 dark:border-strokedark dark:bg-meta-4">
            <div class="p-2 font-medium">Hostname</div>
            <div class="p-2 text-center font-medium">Status</div>
            <div class="p-2 text-center font-medium">UUID</div>
          </div>

          <div class="flex flex-col">
            <div v-if="agents.length === 0" class="p-4 text-center text-gray-500 text-sm">
              호스트 등록 대기중
            </div>

            <div 
                v-for="agent in agents" 
                :key="agent.uuid"  @click="selectAgent(agent)"
                :class="[
                  'grid grid-cols-3 ...',
                  selectedAgent?.uuid === agent.uuid ? 'bg-primary/5 ...' : '' ]"
              >
              <div class="py-2 px-6 flex flex-col gap-1">
                <span class="text-black dark:text-white font-bold text-sm">{{ agent.name }}</span>
                <span class="text-xs text-gray-500 font-medium">{{ agent.ip }}</span>
              </div>
                <div class="py-4 px-4 text-center">
                  <span 
                    :class="{
                      'text-success bg-success-700': agent.status === 'Online',
                      'text-danger bg-error-700': agent.status === 'Offline',
                      'text-warning bg-gray-700': agent.status === 'Checking...'
                    }" 
                    class="px-2 py-1 rounded text-xs font-bold"
                  >
                    {{ agent.status }}
                  </span>
                </div>
              <div class="py-5 px-1 flex flex-col">
                <span class="text-center text-black dark:text-white font-bold text-sm">{{ agent.id }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-span-12 xl:col-span-7">
        
        <div v-if="!selectedAgent" class="flex h-60 items-center justify-center rounded-sm border border-dashed border-stroke bg-gray p-10 dark:border-strokedark dark:bg-meta-4">
          <p class="text-gray-500">방화벽 규칙을 처리할 호스트를 선택해주세요.</p>
        </div>

        <div v-else class="flex flex-col gap-6">
          <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
            <div class="border-b border-stroke py-4 px-6.5 dark:border-strokedark flex justify-between items-center">
              <h3 class="font-bold text-black dark:text-white">Control Panel</h3>
              <span class="text-xs bg-gray-200 dark:bg-gray-700 px-2 py-1 rounded">{{ selectedAgent.name }} ({{ selectedAgent.ip }})</span>
            </div>
            
            <div class="p-6.5">
              <div v-if="alertMessage.show" class="mb-6">
                <Alert :variant="alertMessage.variant" :title="alertMessage.title" :message="alertMessage.message" :showLink="false"/>
              </div>

              <div class="flex flex-col gap-4 sm:flex-row items-end">
                <div class="w-full sm:w-1/2">
                  <label class="mb-2 block text-sm font-medium text-black dark:text-white">Target IP</label>
                  <input 
                    v-model="targetIp" 
                    type="text" 
                    placeholder="e.g. 192.168.100.50" 
                    class="w-full rounded border-[1.5px] border-stroke bg-transparent py-3 px-5 font-medium outline-none transition focus:border-primary active:border-primary dark:border-form-strokedark dark:bg-form-input dark:focus:border-primary"
                  />
                </div>

                <div class="w-full sm:w-1/3 relative" ref="dropdownRef">
                  <label class="mb-2 block text-sm font-medium text-black dark:text-white">패킷 DROP</label>
                  <a href="#" @click.prevent="toggleDropdown" class="flex w-full items-center justify-between gap-2 px-4 py-3 text-sm font-medium text-white rounded bg-gray-800 hover:bg-opacity-90">
                    {{ blockMode }}
                    <svg class="duration-200 ease-in-out stroke-current" :class="{ 'rotate-180': isDropdownOpen }" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M4.79199 7.396L10.0003 12.6043L15.2087 7.396" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </a>
                  <transition enter-active-class="transition duration-200 ease-out" enter-from-class="transform scale-95 opacity-0" enter-to-class="transform scale-100 opacity-100" leave-active-class="transition duration-75 ease-in" leave-from-class="transform scale-100 opacity-100" leave-to-class="transform scale-95 opacity-0">
                    <div v-if="isDropdownOpen" class="absolute left-0 top-full z-40 mt-2 w-full min-w-[150px] rounded-md border border-gray-200 bg-white p-3 shadow-theme-lg dark:border-gray-800 dark:bg-[#1E2635]">
                      <ul class="flex flex-col gap-1">
                        <li v-for="item in dropdownItems" :key="item.text">
                          <a href="#" @click.prevent="handleItemClick(item)" class="flex rounded px-3 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-white/5">
                            {{ item.text }}
                          </a>
                        </li>
                      </ul>
                    </div>
                  </transition>
                </div>

                <div class="w-full sm:w-auto">
                  <Button @click="sendBlockCommand" :disabled="loading" size="md" variant="primary" class="w-full justify-center py-3">
                    {{ loading ? 'Sending...' : 'BLOCK' }}
                  </Button>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
            <div class="py-4 px-6.5 border-b border-stroke dark:border-strokedark">
              <h4 class="font-semibold text-black dark:text-white">Active Rules (Session)</h4>
            </div>
            <div class="grid grid-cols-4 border-b border-stroke py-3 px-4 bg-gray-2 dark:border-strokedark dark:bg-meta-4">
              <div class="font-medium text-sm">IP Address</div>
              <div class="font-medium text-sm text-center">Type</div>
              <div class="font-medium text-sm text-center">Interface</div>
              <div class="font-medium text-sm text-center">Action</div>
            </div>
            
            <div v-if="agentRules.length === 0" class="p-4 text-center text-gray-500 text-sm">
              No active rules added in this session.
            </div>

            <div v-for="(rule, index) in agentRules" :key="index" class="grid grid-cols-4 border-b border-stroke py-3 px-4 dark:border-strokedark items-center">
              <div class="text-sm font-bold text-black dark:text-white">{{ rule.ip }}</div>
              <div class="text-center">
                <span :class="rule.type === 'XDP' ? 'text-warning bg-warning/10' : 'text-primary bg-primary/10'" class="text-xs font-bold px-2 py-1 rounded">
                  {{ rule.type }}
                </span>
              </div>
              <div class="text-sm text-center">{{ rule.interface }}</div>
              <div class="text-center">
                <button @click="handleUnblock(rule, index)" class="text-danger hover:underline text-xs font-medium">
                  Unblock
                </button>
              </div>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  </AdminLayout>
</template>