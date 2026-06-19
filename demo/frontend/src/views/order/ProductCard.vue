<script setup lang="ts">
import type { Shop } from "../../types/shop";

defineProps<{
    product: Shop
}>();

defineEmits<{
    addToCart: [product: Shop]
}>();
</script>

<template>
    <div class="card h-100 border">
        <div class="card-body d-flex flex-column p-3">
            <div class="bg-light rounded d-flex align-items-center justify-content-center mb-2 overflow-hidden">
                <img
                    v-if="product.imagePath"
                    :src="`/api/shop/images/${product.imagePath}`"
                    class="w-100 h-100"
                    style="object-fit: cover;"
                />
                <span v-else class="text-muted" style="font-size: 28px;">&#9744;</span>
            </div>
            <div class="small fw-medium mb-1">{{ product.name }}</div>
            <div class="mb-1">
                <span class="text-danger fw-medium">¥{{ product.price.toLocaleString() }}</span>
            </div>
            <div class="small mb-2" :class="product.stock > 0 ? 'text-success' : 'text-danger'">
                <template v-if="product.stock === 0">在庫なし</template>
                <template v-else-if="product.stock <= 5">残り{{ product.stock }}点</template>
                <template v-else>在庫あり ({{ product.stock }}点)</template>
            </div>
            <div class="mt-auto">
                <button
                    v-if="product.stock > 0"
                    class="btn btn-warning btn-sm w-100 rounded-pill fw-medium"
                    @click="$emit('addToCart', product)"
                >カートに入れる
                </button>
                <button v-else class="btn btn-sm w-100 rounded-pill" disabled>
                    在庫切れ
                </button>
            </div>
        </div>
    </div>
</template>