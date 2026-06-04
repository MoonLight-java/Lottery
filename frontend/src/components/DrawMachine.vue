<script setup>
import { ref, watch, onMounted, computed } from 'vue'
import { getItems, drawSingle, drawMultiple } from '../api/lottery.js'

const props = defineProps({
  refreshKey: Number,
  winners: { type: [String, Array, null], default: null }
})
const emit = defineEmits(['draw-result'])

const items = ref([])
const reelText = ref('准备就绪')
const spinning = ref(false)
const revealing = ref(false)
const multiCount = ref(3)
const probBars = ref([])
const showCatFace = ref(true)
const screenShake = ref(false)

async function loadItems() {
  const data = await getItems()
  items.value = data
  updateProbBars()
}

function updateProbBars() {
  const total = items.value.reduce((s, i) => s + i.weight, 0)
  probBars.value = items.value.map(i => ({
    name: i.name,
    pct: total > 0 ? ((i.weight / total) * 100).toFixed(1) : 0,
    width: total > 0 ? ((i.weight / total) * 100).toFixed(0) : 0
  }))
}

function easeOutCubic(t) {
  return 1 - Math.pow(1 - t, 3)
}
function easeOutQuart(t) {
  return 1 - Math.pow(1 - t, 4)
}

async function spin(winner) {
  spinning.value = true
  revealing.value = false
  showCatFace.value = false
  screenShake.value = false
  const names = items.value.map(i => i.name)
  if (!names.length) {
    reelText.value = '无奖品'
    spinning.value = false
    showCatFace.value = true
    return
  }

  const duration = 2600
  const start = performance.now()
  let lastSwitch = 0
  let currentName = names[Math.floor(Math.random() * names.length)]

  function tick(now) {
    const elapsed = now - start
    if (elapsed >= duration) {
      // Reveal phase
      reelText.value = winner
      spinning.value = false
      revealing.value = true
      screenShake.value = true
      setTimeout(() => {
        revealing.value = false
        screenShake.value = false
      }, 600)
      return
    }
    const raw = elapsed / duration
    // Fast spin for 60% of duration, then decelerate with easing
    let interval
    if (raw < 0.6) {
      interval = 65 - raw * 50  // 65ms → 35ms (speeding up)
    } else {
      const decel = (raw - 0.6) / 0.4  // 0 → 1
      interval = 35 + easeOutQuart(decel) * 500  // 35ms → 535ms
    }
    if (elapsed - lastSwitch > interval) {
      let next
      do { next = names[Math.floor(Math.random() * names.length)] } while (next === currentName && names.length > 1)
      currentName = next
      reelText.value = currentName
      lastSwitch = elapsed
    }
    requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
}

async function spinMulti(winners) {
  spinning.value = true
  revealing.value = false
  showCatFace.value = false
  screenShake.value = false
  const names = items.value.map(i => i.name)
  if (!names.length) return

  const duration = 2400
  const start = performance.now()
  let lastSwitch = 0
  let currentName = names[Math.floor(Math.random() * names.length)]

  function tick(now) {
    const elapsed = now - start
    if (elapsed >= duration) {
      reelText.value = winners[0]
      spinning.value = false
      revealing.value = true
      screenShake.value = true
      // Reveal remaining winners one by one
      if (winners.length > 1) {
        let idx = 1
        const iv = setInterval(() => {
          reelText.value = winners.slice(0, idx + 1).join('、')
          idx++
          if (idx >= winners.length) clearInterval(iv)
        }, 500)
      }
      setTimeout(() => {
        revealing.value = false
        screenShake.value = false
      }, 600 + (winners.length - 1) * 500)
      return
    }
    const raw = elapsed / duration
    let interval
    if (raw < 0.6) {
      interval = 65 - raw * 50
    } else {
      const decel = (raw - 0.6) / 0.4
      interval = 35 + easeOutQuart(decel) * 500
    }
    if (elapsed - lastSwitch > interval) {
      let next
      do { next = names[Math.floor(Math.random() * names.length)] } while (next === currentName && names.length > 1)
      currentName = next
      reelText.value = currentName
      lastSwitch = elapsed
    }
    requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
}

async function handleSingleDraw() {
  if (spinning.value || !items.value.length) return
  const res = await drawSingle()
  if (res.success) {
    await spin(res.winner)
    emit('draw-result', res.winner)
  } else alert(res.message)
}

async function handleMultiDraw() {
  if (spinning.value || !items.value.length) return
  const count = Math.max(1, Math.min(multiCount.value, items.value.length))
  const res = await drawMultiple(count)
  if (res.success) {
    await spinMulti(res.winners)
    emit('draw-result', res.winners)
  } else alert(res.message)
}

watch(() => props.refreshKey, loadItems)
watch(() => props.winners, (val) => {
  if (val === null) { reelText.value = '准备就绪'; showCatFace.value = true }
})
onMounted(loadItems)
</script>

<template>
  <main class="panel panel--center">
    <div class="machine">
      <div class="machine__crown">
        <span></span><span></span><span></span>
      </div>
      <h1>✨ 幸 运 转 盘 ✨</h1>

      <div class="machine__screen" :class="{ 'is-spinning': spinning, 'is-reveal': revealing, 'is-shake': screenShake }">
        <div class="machine__inner">
          <!-- Idle Hello Kitty -->
          <img v-if="showCatFace && !spinning" src="/hello-kitty-icon%20(3).jpeg" alt="Hello Kitty" class="machine__kitty" />
          <span class="machine__label">Draw Result</span>
          <div class="machine__reel" :class="{ 'is-spinning': spinning, 'is-reveal': revealing }">
            <span class="machine__result">{{ reelText }}</span>
          </div>
        </div>
      </div>

      <div class="machine__lights">
        <span v-for="i in 7" :key="i" class="light" :class="i % 2 === 1 ? 'light--pink' : 'light--lavender'"></span>
      </div>

      <div class="machine__actions">
        <button class="btn--draw-main" @click="handleSingleDraw" :disabled="spinning || !items.length">
          <span class="btn__spark"></span>
          🎰 单次抽奖
        </button>
        <div class="multi-row">
          <input v-model.number="multiCount" type="number" min="1" :max="items.length || 99" class="multi-input" />
          <button class="btn--draw-sub" @click="handleMultiDraw" :disabled="spinning || !items.length">
            🎁 多人抽奖
          </button>
        </div>
      </div>
    </div>

    <div class="prob-section" v-if="probBars.length">
      <h3>
        <img src="/hello-kitty-icon%20(5).jpeg" alt="Hello Kitty" class="kitty-decor kitty-decor--lg" />
        📊 中奖概率分布
      </h3>
      <div class="prob-row" v-for="b in probBars" :key="b.name">
        <span class="prob-row__name">{{ b.name }}</span>
        <div class="prob-row__track">
          <div class="prob-row__fill" :style="{ width: b.width + '%' }"></div>
        </div>
        <span class="prob-row__pct">{{ b.pct }}%</span>
      </div>
    </div>
  </main>
</template>
