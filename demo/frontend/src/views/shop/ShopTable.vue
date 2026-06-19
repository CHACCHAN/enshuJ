<script setup lang="ts">
import StockBadge from '../../components/StockBadge.vue'
import type { Shop } from "../../types/shop";

defineProps<{
    shops: Shop[]
}>()

defineEmits<{
    edit: [item: Shop]
    delete: [item: Shop]
}>()
</script>

<template>
    <table class="table table-hover align-middle">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>商品名</th>
                <th>値段</th>
                <th>在庫</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="item in shops" :key="item.id">
                <td class="text-muted small">{{ item.id }}</td>
                <td>{{ item.name }}</td>
                <td>¥{{ item.price.toLocaleString() }}</td>
                <td><StockBadge :stock="item.stock" /></td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-secondary me-1" @click="$emit('edit', item)">編集</button>
                    <button class="btn btn-sm btn-outline-danger" @click="$emit('delete', item)">削除</button>
                </td>
            </tr>
            <tr v-if="shops.length === 0">
                <td colspan="5" class="text-center text-muted py-4">商品がありません</td>
            </tr>
        </tbody>
  </table>
</template>
