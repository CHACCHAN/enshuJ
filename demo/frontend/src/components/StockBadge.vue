<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
    stock: number
}>()

const badgeClass = computed(() => {
    if (props.stock === 0) return 'bg-danger'
    if (props.stock <= 5) return 'bg-warning text-dark'
    return 'bg-success'
})

const label = computed(() => {
    if (props.stock === 0) return '在庫なし'
    if (props.stock <= 5) return `残り${props.stock}点`
    return `${props.stock}点`
})
</script>

<template>
    <span class="badge" :class="badgeClass">{{ label }}</span>
</template>