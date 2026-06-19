<script setup lang="ts">
import { ref, watch } from "vue";
import type { Shop } from "../../types/shop";
import ImageCropper from "../../components/ImageCropper.vue";

const props = defineProps<{
    initialData: Shop | null
}>();

const emit = defineEmits<{
    submit: [form: Shop, image?: File]
}>();

const isEdit = ref(false);
const form = ref<Shop>({ name: "", price: 0, stock: 0 });
const croppedImage = ref<File | undefined>();

watch(() => props.initialData, (val) => {
    croppedImage.value = undefined
    if (val) {
        isEdit.value = true;
        form.value = { ...val };
    } else {
        isEdit.value = false;
        form.value = { name: "", price: 0, stock: 0 };
    }
});

function onCropped(file: File): void {
    croppedImage.value = file;
}

function submit(): void {
    emit("submit", { ...form.value }, croppedImage.value);
}
</script>

<template>
    <div class="modal fade" id="shopModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header border-0">
                    <h5 class="modal-title">{{ isEdit ? '商品を編集' : '商品を登録' }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" />
                </div>
                <div class="modal-body">
                    <div class="row g-4" style="align-items: stretch;">

                        <!-- 左: フォーム -->
                        <div class="col-md-6 d-flex flex-column">
                        <div class="mb-3">
                            <label class="form-label">商品名</label>
                            <input v-model="form.name" type="text" class="form-control" placeholder="例：りんご" />
                        </div>
                        <div class="mb-3">
                            <label class="form-label">値段（円）</label>
                            <input v-model.number="form.price" type="number" class="form-control" min="0" />
                        </div>
                        <div class="mb-3">
                            <label class="form-label">在庫数</label>
                            <input v-model.number="form.stock" type="number" class="form-control" min="0" />
                        </div>
                        </div>

                        <!-- 右: 画像エディタ（左と同じ高さ） -->
                        <div class="col-md-6 d-flex flex-column">
                            <label class="form-label">商品画像</label>
                            <ImageCropper :aspectRatio="1" :previewSize="80" :fillHeight="true" @cropped="onCropped" />

                            <div v-if="isEdit && form.imagePath && !croppedImage" class="mt-2">
                                <p class="small text-muted mb-1">現在の画像</p>
                                <img
                                    :src="`/api/shop/images/${form.imagePath}`"
                                    class="rounded border"
                                    style="width: 80px; height: 80px; object-fit: cover;"
                                />
                            </div>
                        </div>

                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">キャンセル</button>
                    <button class="btn btn-primary" @click="submit" data-bs-dismiss="modal">
                        {{ isEdit ? '更新' : '登録' }}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

