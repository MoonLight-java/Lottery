<script setup>
import { ref, onMounted } from 'vue'
import { getItems, addItem, removeItem, clearItems } from '../api/lottery.js'
import ConfirmModal from './ConfirmModal.vue'

const emit = defineEmits(['items-changed'])
const items = ref([])
const name = ref('')
const weight = ref(10)
const showClearModal = ref(false)

async function loadItems() {
  const data = await getItems()
  items.value = data
}
function totalWeight() {
  return items.value.reduce((s, i) => s + i.weight, 0)
}
function probability(item) {
  const tw = totalWeight()
  if (tw === 0) return 0
  return ((item.weight / tw) * 100).toFixed(1)
}
async function handleAdd() {
  if (!name.value.trim()) return
  const res = await addItem({ name: name.value.trim(), weight: weight.value })
  if (res.success) {
    name.value = ''
    weight.value = 10
    await loadItems()
    emit('items-changed')
  } else {
    alert(res.message)
  }
}
async function handleRemove(itemName) {
  await removeItem(itemName)
  await loadItems()
  emit('items-changed')
}
async function handleClear() {
  showClearModal.value = true
}
async function confirmClear() {
  showClearModal.value = false
  await clearItems()
  await loadItems()
  emit('items-changed')
}
onMounted(loadItems)
</script>

<template>
  <aside class="panel panel--left">
    <div class="panel__head">
      <h2>🎀 奖品管理</h2>
      <span class="badge">{{ items.length }} 项</span>
    </div>

    <form class="add-form" @submit.prevent="handleAdd">
      <div class="field">
        <input v-model="name" type="text" placeholder="输入奖品名称" autocomplete="off" />
      </div>
      <div class="field">
        <input v-model.number="weight" type="number" placeholder="权重数值" min="1" />
        <span class="field__hint">权重越高，中奖概率越大</span>
      </div>
      <button type="submit" class="btn btn--primary">+ 添加奖品</button>
    </form>

    <div class="sep"></div>

    <ul class="item-list" v-if="items.length">
      <li v-for="item in items" :key="item.name" class="item-row">
        <div class="item-row__info">
          <span class="item-row__name">{{ item.name }}</span>
          <span class="item-row__prob">{{ probability(item) }}%</span>
        </div>
        <div class="item-row__bar">
          <div class="item-row__fill" :style="{ width: probability(item) + '%' }"></div>
        </div>
        <button class="item-row__del" @click="handleRemove(item.name)" title="删除">×</button>
      </li>
    </ul>

    <!-- Empty state with Hello Kitty -->
    <div v-else class="empty-hint">
      <img src="/hello-kitty-bow.png" alt="Hello Kitty" class="empty-kitty" />
      <span>还没有奖品哦~ 在上方添加吧</span>
    </div>

    <button v-if="items.length" class="btn btn--ghost" @click="handleClear">清空全部奖品</button>

    <ConfirmModal
      :show="showClearModal"
      title="清空奖品"
      message="确定要清空所有奖品吗？此操作不可恢复。"
      @confirm="confirmClear"
      @cancel="showClearModal = false"
    />
  </aside>
</template>
