<script setup>
defineProps({
  show: Boolean,
  title: { type: String, default: '确认操作' },
  message: { type: String, default: '' },
  confirmText: { type: String, default: '确定' },
  cancelText: { type: String, default: '取消' }
})
const emit = defineEmits(['confirm', 'cancel'])
</script>

<template>
  <Teleport to="body">
    <div v-if="show" class="modal-overlay" @click.self="emit('cancel')">
      <div class="modal-card">
        <div class="modal-card__icon">🎀</div>
        <h3 class="modal-card__title">{{ title }}</h3>
        <p class="modal-card__msg">{{ message }}</p>
        <div class="modal-card__actions">
          <button class="modal-card__btn modal-card__btn--cancel" @click="emit('cancel')">
            {{ cancelText }}
          </button>
          <button class="modal-card__btn modal-card__btn--confirm" @click="emit('confirm')">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>
