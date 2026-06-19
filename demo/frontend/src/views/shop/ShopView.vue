<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Modal } from "bootstrap";
import ShopTable from "./ShopTable.vue";
import ShopModal from "./ShopModal.vue";
import ConfirmModal from "../../components/ConfirmModal.vue";
import type { Shop } from "../../types/shop";
import { shopApi } from "../../api/shop.ts";
import { RouterLink } from "vue-router";

const shops = ref<Shop[]>([])
const editTarget = ref<Shop | null>(null)
const deleteTarget = ref<Shop | null>(null)

async function fetchShops(): Promise<void> {
    const res = await fetch("/api/shop")
    shops.value = await res.json() as Shop[]
}

function openCreate(): void {
    editTarget.value = null
}

function openEdit(item: Shop): void {
    editTarget.value = { ...item }
    new Modal(document.getElementById("shopModal")!).show()
}

function openDelete(item: Shop): void {
    deleteTarget.value = item
    new Modal(document.getElementById("confirmModal")!).show()
}

async function handleSubmit(form: Shop): Promise<void> {
    if (form.id) {
        await shopApi.update(form.id, form)
    } else {
        await shopApi.create(form)
    }
    fetchShops()
}

async function handleDelete(): Promise<void> {
    if (!deleteTarget.value?.id) return
    await shopApi.remove(deleteTarget.value.id)
    fetchShops()
}

onMounted(fetchShops)
</script>

<template>
    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h4 mb-0">商品管理</h1>
            <div class="d-flex gap-2 align-items-center">
                <RouterLink to="/" class="btn btn-danger">
                    ← 戻る
                </RouterLink>
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#shopModal" @click="openCreate">
                    + 商品を追加
                </button>
            </div>
        </div>

        <ShopTable :shops="shops" @edit="openEdit" @delete="openDelete" />

        <ShopModal :initialData="editTarget" @submit="handleSubmit" />

        <ConfirmModal
            modalId="confirmModal"
            title="商品の削除"
            :message="`「${deleteTarget?.name}」を削除しますか？`"
            @confirm="handleDelete"
        />
    </div>
</template>
