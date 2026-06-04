<script setup>
import { ref, watch, onMounted } from 'vue'
import { getHistory, clearHistory as clearHistoryApi } from '../api/lottery.js'
import ConfirmModal from './ConfirmModal.vue'

const props = defineProps({
  refreshKey: Number,
  winners: { type: [String, Array, null], default: null }
})
const records = ref([])
const showClearModal = ref(false)

function formatTime(ts) {
  const d = new Date(ts)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}
async function load() {
  const data = await getHistory()
  records.value = data.reverse()
}
async function handleClear() {
  showClearModal.value = true
}
async function confirmClear() {
  showClearModal.value = false
  await clearHistoryApi()
  records.value = []
}
watch(() => props.refreshKey, load)
watch(() => props.winners, load)
onMounted(load)
</script>

<template>
  <aside class="panel panel--right">
    <div class="panel__head">
      <h2>📝 抽奖记录</h2>
      <span class="badge">{{ records.length }} 条</span>
    </div>

    <ul class="history-list" v-if="records.length">
      <li v-for="(r, i) in records" :key="i" class="history-item">
        <span class="history-item__idx">{{ records.length - i }}</span>
        <span class="history-item__name">{{ r.winner }}</span>
        <span class="history-item__time">{{ formatTime(r.timestamp) }}</span>
      </li>
    </ul>

    <!-- Empty state with Hello Kitty -->
    <div v-else class="empty-hint">
      <img src="/hello-kitty-cute.png" alt="Hello Kitty" class="empty-kitty" />
      <span>还没有记录~ 来试试手气吧</span>
    </div>

    <img src="/hello-kitty-icon%20(4).jpeg" alt="Hello Kitty" class="panel-corner-kitty" />

    <button v-if="records.length" class="btn btn--ghost" @click="handleClear">清空记录</button>

    <ConfirmModal
      :show="showClearModal"
      title="清空记录"
      message="确定要清空所有抽奖记录吗？此操作不可恢复。"
      @confirm="confirmClear"
      @cancel="showClearModal = false"
    />
  </aside>
</template>
