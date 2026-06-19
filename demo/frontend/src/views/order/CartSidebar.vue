<script setup lang="ts">
import type { CartItem } from "../../types/cart";

defineProps<{
    items: CartItem[]
    totalItems: number
    totalPrice: number
}>();

defineEmits<{
    updateQuantity: [productId: number, quantity: number]
    removeItem: [productId: number]
    checkout: []
}>();
</script>

<template>
    <div class="card border">
        <div class="card-body p-3">
            <h6 class="fw-medium mb-3">カート ({{ totalItems }}点)</h6>

            <div v-if="items.length === 0" class="text-muted small text-center py-3">
                カートは空です
            </div>

            <div v-for="item in items" :key="item.product.id" class="d-flex align-items-center justify-content-between py-2 border-bottom">
                <div class="me-2">
                    <div class="small fw-medium">{{ item.product.name }}</div>
                    <div class="text-muted" style="font-size: 12px;">
                        ¥{{ item.product.price.toLocaleString() }}
                    </div>
                </div>
                <div class="d-flex align-items-center gap-1">
                    <button
                        class="btn btn-outline-secondary btn-sm rounded-circle p-0 d-flex align-items-center justify-content-center"
                        style="width: 24px; height: 24px; font-size: 14px;"
                        @click="$emit('updateQuantity', item.product.id!, item.quantity - 1)"
                    >&minus;
                    </button>
                    <span class="small fw-medium" style="min-width: 20px; text-align: center;">{{ item.quantity }}</span>
                    <button
                        class="btn btn-outline-secondary btn-sm rounded-circle p-0 d-flex align-items-center justify-content-center"
                        style="width: 24px; height: 24px; font-size: 14px;"
                        :disabled="item.quantity >= item.product.stock"
                        @click="$emit('updateQuantity', item.product.id!, item.quantity + 1)"
                    >+
                    </button>
                    <button
                        class="btn btn-sm text-danger p-0 ms-1"
                        style="font-size: 14px;"
                        @click="$emit('removeItem', item.product.id!)"
                    >&times;
                    </button>
                </div>
            </div>

            <div v-if="items.length > 0">
                <div class="d-flex justify-content-between fw-medium py-2">
                    <span>合計</span>
                    <span>¥{{ totalPrice.toLocaleString() }}</span>
                </div>
                <button
                    class="btn btn-warning w-100 rounded-pill fw-medium mt-1"
                    @click="$emit('checkout')"
                >レジに進む
                </button>
            </div>
        </div>
    </div>
</template>