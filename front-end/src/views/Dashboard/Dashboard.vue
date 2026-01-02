<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import Breadcrumb from '@/components/common/PageBreadcrumb.vue'
import Alert from '@/components/ui/Alert.vue'
import Button from '@/components/ui/Button.vue'
import api from '@/api/axios'
import GridIcon from '@/icons/GridIcon.vue'
import TaskIcon from '@/icons/TaskIcon.vue'


// --- 1. 상태 변수 ---
const pageTitle = ref('Security Control Center')
const selectedAgent = ref<any>(null)
const loading = ref(false)
const targetIp = ref('')
const blockMode = ref('XDP') // 기본값

// 드롭다운 관련 상태 (주신 코드 기반)
const isDropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const dropdownItems = [
  { text: 'XDP (L2 Drop)', value: 'XDP' },
  { text: 'NFTables (L3 Drop)', value: 'NFTables' }
]

// 알림 메시지 상태
const alertMessage = ref({ show: false, variant: 'success', title: '', message: '' })

// (임시 데이터)
const agents = ref([
  { id: 'uuid-01', name: 'Web-Server-01', ip: '192.168.0.10', status: 'Active', drop_rate: '1231 pkts' },
  { id: 'uuid-02', name: 'DB-Master', ip: '192.168.0.20', status: 'Pending', drop_rate: '10195 pkts' },
])
const agentRules = ref([
  { ip: '1.2.3.4', type: 'XDP', interface: 'eth0', status: 'Active' },
  { ip: '5.6.7.8', type: 'NFTables', interface: 'ALL', status: 'Active' },
])

// --- 2. 드롭다운 로직 (주신 코드 통합) ---
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

const closeDropdown = () => {
  isDropdownOpen.value = false
}

// 드롭다운 아이템 클릭 시 모드 변경
const handleItemClick = (item: any) => {
  blockMode.value = item.value
  closeDropdown()
}

// 외부 클릭 감지 (v-click-outside 디렉티브 없이 순수 JS로 구현)
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

// --- 3. 동작 함수 ---
const selectAgent = (agent: any) => {
  selectedAgent.value = agent
  alertMessage.value.show = false
}

const sendBlockCommand = async () => {
  if (!targetIp.value || !selectedAgent.value) return
  
  loading.value = true
  alertMessage.value.show = false

  try {
    await api.post(`/agents/${selectedAgent.value.id}/xdp/commands`, {
      action: 'ADD',
      targetIp: targetIp.value,
      interfaceName: 'eth0',
      mode: blockMode.value === 'XDP' ? 'src' : 'nft'
    })

    agentRules.value.push({
      ip: targetIp.value,
      type: blockMode.value,
      interface: 'eth0',
      status: 'Pending'
    })

    alertMessage.value = {
      show: true,
      variant: 'success',
      title: 'Block Command Sent',
      message: `Blocked ${targetIp.value} via ${blockMode.value}`
    }
    targetIp.value = ''

  } catch (e) {
    alertMessage.value = {
      show: true,
      variant: 'danger',
      title: 'Command Failed',
      message: 'Server connection failed.'
    }
  } finally {
    loading.value = false
  }
}

// 라이프사이클 훅 (드롭다운 외부 클릭 감지용)
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
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
            <div class="p-2 text-center font-medium">Drop</div>
          </div>
          <div class="flex flex-col">
            <div 
              v-for="agent in agents" 
              :key="agent.id" 
              @click="selectAgent(agent)"
              :class="[
                'grid grid-cols-3 border-b border-stroke dark:border-strokedark cursor-pointer hover:bg-gray-100 dark:hover:bg-meta-4 transition items-center py-3 px-4',
                selectedAgent?.id === agent.id ? 'bg-primary/5 border-l-4 border-l-primary' : ''
              ]"
            >
              <div class="flex flex-col">
                <span class="text-black dark:text-white font-bold text-sm">{{ agent.name }}</span>
                <span class="text-xs text-gray-900">{{ agent.ip }}</span>
              </div>
              <div class="text-center">
                <span :class="agent.status === 'Active' ? 'text-success bg-success/10' : 'text-warning bg-warning/10'" class="px-2 py-1 rounded text-xs font-bold">{{ agent.status }}</span>
              </div>
              <div class="text-center text-sm">{{ agent.drop_rate }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-span-12 xl:col-span-7">
        
        <div v-if="!selectedAgent" class="flex h-60 items-center justify-center rounded-sm border border-dashed border-stroke bg-gray p-10 dark:border-strokedark dark:bg-meta-4">
          <p class="text-gray-500">👈 Select an agent to manage security rules.</p>
        </div>

        <div v-else class="flex flex-col gap-6">
          <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
            <div class="border-b border-stroke py-4 px-6.5 dark:border-strokedark flex justify-between items-center">
              <h3 class="font-bold text-black dark:text-white">Control Panel</h3>
              <span class="text-xs bg-gray-200 dark:bg-gray-700 px-2 py-1 rounded">{{ selectedAgent.name }}</span>
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
                    placeholder="1.2.3.4" 
                    class="w-full rounded border-[1.5px] border-stroke bg-transparent py-3 px-5 font-medium outline-none transition focus:border-primary active:border-primary dark:border-form-strokedark dark:bg-form-input dark:focus:border-primary"
                  />
                </div>

                <div class="w-full sm:w-1/3 relative" ref="dropdownRef">
                  <label class="mb-2 block text-sm font-medium text-black dark:text-white">Block Mode</label>
                  
                  <a
                    href="#"
                    @click.prevent="toggleDropdown"
                    class="flex w-full items-center justify-between gap-2 px-4 py-3 text-sm font-medium text-white rounded bg-gray-800 hover:bg-opacity-90"
                  >
                    {{ blockMode }}
                    <svg
                      class="duration-200 ease-in-out stroke-current"
                      :class="{ 'rotate-180': isDropdownOpen }"
                      width="20"
                      height="20"
                      viewBox="0 0 20 20"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M4.79199 7.396L10.0003 12.6043L15.2087 7.396"
                        stroke="currentColor"
                        stroke-width="1.5"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                  </a>

                  <transition
                    enter-active-class="transition duration-200 ease-out"
                    enter-from-class="transform scale-95 opacity-0"
                    enter-to-class="transform scale-100 opacity-100"
                    leave-active-class="transition duration-75 ease-in"
                    leave-from-class="transform scale-100 opacity-100"
                    leave-to-class="transform scale-95 opacity-0"
                  >
                    <div
                      v-if="isDropdownOpen"
                      class="absolute left-0 top-full z-40 mt-2 w-full min-w-[150px] rounded-md border border-gray-200 bg-white p-3 shadow-theme-lg dark:border-gray-800 dark:bg-[#1E2635]"
                    >
                      <ul class="flex flex-col gap-1">
                        <li v-for="item in dropdownItems" :key="item.text">
                          <a
                            href="#"
                            @click.prevent="handleItemClick(item)"
                            class="flex rounded px-3 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-white/5"
                          >
                            {{ item.text }}
                          </a>
                        </li>
                      </ul>
                    </div>
                  </transition>
                </div>
                <div class="w-full sm:w-auto">
                  <Button @click="sendBlockCommand" :disabled="loading" size="md" variant="primary" class="w-full justify-center py-3">
                    {{ loading ? '...' : 'BLOCK' }}
                  </Button>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
            <div class="py-4 px-6.5 border-b border-stroke dark:border-strokedark">
              <h4 class="font-semibold text-black dark:text-white">Active Rules</h4>
            </div>
            <div class="grid grid-cols-4 border-b border-stroke py-3 px-4 bg-gray-2 dark:border-strokedark dark:bg-meta-4">
              <div class="font-medium text-sm">IP Address</div>
              <div class="font-medium text-sm text-center">Type</div>
              <div class="font-medium text-sm text-center">Interface</div>
              <div class="font-medium text-sm text-center">Action</div>
            </div>
            <div v-for="(rule, key) in agentRules" :key="key" class="grid grid-cols-4 border-b border-stroke py-3 px-4 dark:border-strokedark items-center">
              <div class="text-sm font-bold text-black dark:text-white">{{ rule.ip }}</div>
              <div class="text-center"><span :class="rule.type === 'XDP' ? 'text-warning bg-warning/10' : 'text-primary bg-primary/10'" class="text-xs font-bold px-2 py-1 rounded">{{ rule.type }}</span></div>
              <div class="text-sm text-center">{{ rule.interface }}</div>
              <div class="text-center"><button class="text-danger hover:underline text-xs font-medium">Unblock</button></div>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  </AdminLayout>
</template>