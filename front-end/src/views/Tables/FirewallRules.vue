<script setup lang="ts">
import { ref } from 'vue'
// ▼ [수정됨] 사용자님의 실제 파일 경로로 변경
import AdminLayout from '@/components/layout/AdminLayout.vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import api from '@/api/axios'

const pageTitle = ref('Firewall Rules')
const targetIp = ref('')
const loading = ref(false)
// 백엔드 로그에서 확인한 Agent ID (테스트용)
const agentId = 'd4484382-4e58-41b2-bef3-26d276a2236e' 

// (임시) 화면에 보여줄 차단 목록 더미 데이터
const rules = ref([
  { id: 1, ip: '1.2.3.4', interface: 'eth0', status: 'Active', type: 'XDP' },
  { id: 2, ip: '5.6.7.8', interface: 'eth0', status: 'Active', type: 'NFTables' },
])

// 차단 요청 함수
const sendBlockCommand = async () => {
  if (!targetIp.value) return

  loading.value = true
  try {
    // 백엔드로 POST 요청
    await api.post(`/agents/${agentId}/xdp/commands`, {
      action: 'ADD',
      targetIp: targetIp.value,
      interfaceName: 'eth0',
      mode: 'src'
    })
    
    // 성공 시 목록에 추가 (UI 업데이트)
    rules.value.push({
      id: Date.now(),
      ip: targetIp.value,
      interface: 'eth0',
      status: 'Pending',
      type: 'XDP'
    })
    
    alert(`[성공] ${targetIp.value} 차단 명령 전송 완료`)
    targetIp.value = ''
    
  } catch (error) {
    console.error(error)
    alert('차단 실패! 백엔드 연결을 확인하세요.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AdminLayout>
    
    <PageBreadcrumb :pageTitle="pageTitle" />

    <div class="flex flex-col gap-10">
      
      <div class="rounded-sm border border-stroke bg-white shadow-default dark:border-strokedark dark:bg-boxdark">
        <div class="border-b border-stroke py-4 px-6.5 dark:border-strokedark">
          <h3 class="font-medium text-black dark:text-white">
            🚫 New Block Rule
          </h3>
        </div>
        
        <div class="p-6.5">
          <div class="mb-4.5">
            <label class="mb-2.5 block text-black dark:text-white">
              Target IP Address <span class="text-meta-1">*</span>
            </label>
            <input
              v-model="targetIp"
              type="text"
              placeholder="차단할 IP를 입력하세요 (예: 8.8.8.8)"
              class="w-full rounded border-[1.5px] border-stroke bg-transparent py-3 px-5 font-medium outline-none transition focus:border-primary active:border-primary disabled:cursor-default disabled:bg-whiter dark:border-form-strokedark dark:bg-form-input dark:focus:border-primary"
            />
          </div>

          <button
            @click="sendBlockCommand"
            :disabled="loading"
            class="flex w-full justify-center rounded bg-primary p-3 font-medium text-gray hover:bg-opacity-90 disabled:bg-slate-500"
          >
            {{ loading ? '전송 중...' : '즉시 차단 적용' }}
          </button>
        </div>
      </div>

      <div class="rounded-sm border border-stroke bg-white px-5 pt-6 pb-2.5 shadow-default dark:border-strokedark dark:bg-boxdark sm:px-7.5 xl:pb-1">
        <h4 class="mb-6 text-xl font-semibold text-black dark:text-white">
          Active Block List
        </h4>

        <div class="flex flex-col">
          <div class="grid grid-cols-3 rounded-sm bg-gray-2 dark:bg-meta-4 sm:grid-cols-4">
            <div class="p-2.5 xl:p-5"><h5 class="text-sm font-medium uppercase xsm:text-base">IP Address</h5></div>
            <div class="p-2.5 text-center xl:p-5"><h5 class="text-sm font-medium uppercase xsm:text-base">Interface</h5></div>
            <div class="p-2.5 text-center xl:p-5"><h5 class="text-sm font-medium uppercase xsm:text-base">Type</h5></div>
            <div class="hidden p-2.5 text-center sm:block xl:p-5"><h5 class="text-sm font-medium uppercase xsm:text-base">Status</h5></div>
          </div>

          <div
            v-for="(rule, key) in rules"
            :key="key"
            class="grid grid-cols-3 border-b border-stroke dark:border-strokedark sm:grid-cols-4"
          >
            <div class="flex items-center gap-3 p-2.5 xl:p-5">
              <p class="hidden text-black dark:text-white sm:block">{{ rule.ip }}</p>
            </div>

            <div class="flex items-center justify-center p-2.5 xl:p-5">
              <p class="text-black dark:text-white">{{ rule.interface }}</p>
            </div>

            <div class="flex items-center justify-center p-2.5 xl:p-5">
               <span :class="rule.type === 'XDP' ? 'bg-warning text-warning' : 'bg-primary text-primary'" 
                     class="inline-flex rounded-full bg-opacity-10 py-1 px-3 text-sm font-medium">
                 {{ rule.type }}
               </span>
            </div>

            <div class="hidden items-center justify-center p-2.5 sm:flex xl:p-5">
              <p class="text-meta-3">{{ rule.status }}</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </AdminLayout>
</template>