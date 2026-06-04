<script setup>
import ItemPanel from './components/ItemPanel.vue'
import DrawMachine from './components/DrawMachine.vue'
import HistoryPanel from './components/HistoryPanel.vue'
import { ref, onMounted, onUnmounted } from 'vue'
import { checkHealth } from './api/lottery.js'

const refreshKey = ref(0)
const winners = ref(null)
const backendOnline = ref(false)

let timer = null

async function ping() {
  backendOnline.value = await checkHealth()
}

function onItemsChanged() { refreshKey.value++ }
function onDrawResult(result) { winners.value = result }

onMounted(() => {
  ping()
  timer = setInterval(ping, 5000)
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <!-- Side background images -->
  <img src="/left-background.jpeg" alt="" class="bg-left" />
  <img src="/background.jpg" alt="" class="bg-right" />

  <!-- Title Bar -->
  <header class="titlebar">
    <div class="titlebar__left">
      <img src="/hello-kitty-icon%20(2).jpeg" alt="Hello Kitty" class="titlebar__kitty" />
      <h1 class="titlebar__title">Lucky Draw · 幸运抽奖</h1>
    </div>
    <div class="titlebar__right">
      <img src="/hello-kitty-icon.jpeg" alt="Hello Kitty" class="titlebar__kitty-right" />
      <span class="titlebar__dot" :class="{ 'is-offline': !backendOnline }"></span>
      <span>{{ backendOnline ? '系统运行中' : '服务离线' }}</span>
    </div>
  </header>

  <!-- Main Layout -->
  <div class="app-shell">
    <ItemPanel @items-changed="onItemsChanged" />
    <DrawMachine
      :refresh-key="refreshKey"
      :winners="winners"
      @draw-result="onDrawResult"
    />
    <HistoryPanel :refresh-key="refreshKey" :winners="winners" />
  </div>
</template>
