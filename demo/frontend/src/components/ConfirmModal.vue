<script setup lang="ts">
withDefaults(defineProps<{
    modalId: string
    title?: string
    message?: string
}>(), {
    title: '確認',
    message: '本当に削除しますか？'
})

defineEmits<{
    confirm: []
}>()
</script>

<template>
    <div class="modal fade" :id="modalId" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header border-0">
                    <h5 class="modal-title">{{ title }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" />
                </div>
                <div class="modal-body text-muted">{{ message }}</div>
                <div class="modal-footer border-0">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">キャンセル</button>
                    <button class="btn btn-danger" @click="$emit('confirm')" data-bs-dismiss="modal">削除</button>
                </div>
            </div>
        </div>
    </div>
</template>
