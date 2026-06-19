<script setup lang="ts">
import { ref, onBeforeUnmount, nextTick } from "vue";
import Cropper from "cropperjs";

const props = withDefaults(defineProps<{
    aspectRatio?: number
    previewSize?: number
    fillHeight?: boolean
}>(), {
    aspectRatio: 1,
    previewSize: 80,
    fillHeight: false,
});

const emit = defineEmits<{
    cropped: [file: File]
}>();

const fileInput = ref<HTMLInputElement>();
const cropperImage = ref<HTMLImageElement>();
const imageSrc = ref<string | null>(null);
const croppedPreview = ref<string | null>(null);
let cropper: Cropper | null = null;

function triggerFileInput(): void {
    fileInput.value?.click();
}

async function onFileSelected(e: Event): Promise<void> {
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = async () => {
        imageSrc.value = reader.result as string;
        croppedPreview.value = null;
        await nextTick();
        initCropper();
    };
    reader.readAsDataURL(file);
}

function initCropper(): void {
    destroyCropper();
    if (!cropperImage.value) return;
    cropper = new Cropper(cropperImage.value);

    const selection = cropper.getCropperSelection();
    if (selection) {
        selection.aspectRatio = props.aspectRatio;
        selection.initialAspectRatio = props.aspectRatio;
    }
}

async function crop(): Promise<void> {
    if (!cropper) return;
    const selection = cropper.getCropperSelection();
    if (!selection) return;

    const canvas = await selection.$toCanvas({ width: 600, height: 600 });

    croppedPreview.value = canvas.toDataURL("image/jpeg");

    canvas.toBlob((blob) => {
        if (!blob) return;
        const file = new File([blob], "cropped.jpg", { type: "image/jpeg" });
        emit("cropped", file);
    }, "image/jpeg", 0.9);
}

function reset(): void {
    destroyCropper();
    imageSrc.value = null;
    croppedPreview.value = null;
    if (fileInput.value) fileInput.value.value = "";
}

function destroyCropper(): void {
    cropper?.destroy();
    cropper = null;
}

onBeforeUnmount(destroyCropper);
</script>

<template>
    <div :class="{ 'd-flex flex-column h-100': fillHeight }">
        <div
            v-if="!imageSrc"
            class="border rounded-3 bg-light d-flex flex-column align-items-center justify-content-center p-4"
            :class="{ 'flex-grow-1': fillHeight }"
            style="cursor: pointer; min-height: 200px;"
            @click="triggerFileInput"
        >
            <span class="text-muted mb-1" style="font-size: 32px;">&#x2b;</span>
            <span class="text-muted small">クリックして画像を選択</span>
            <input ref="fileInput" type="file" accept="image/*" class="d-none" @change="onFileSelected" />
        </div>

        <div v-else class="d-flex flex-column" :class="{ 'flex-grow-1': fillHeight }">
            <div
                ref="cropperContainer"
                class="border rounded-3 position-relative"
                :class="{ 'flex-grow-1': fillHeight }"
                :style="fillHeight ? 'min-height: 400px;' : 'height: 400px;'"
            >
                <img
                    ref="cropperImage"
                    :src="imageSrc"
                    style="display: block; width: 100%; height: 100%; object-fit: contain;"
                />
            </div>
            <div class="d-flex gap-2 mt-2">
                <button type="button" class="btn btn-sm btn-outline-secondary" @click="reset">画像を変更</button>
                <button type="button" class="btn btn-sm btn-outline-primary" @click="crop">トリミング確定</button>
            </div>
        </div>

        <div v-if="croppedPreview" class="mt-2">
            <p class="small text-muted mb-1">プレビュー</p>
            <img :src="croppedPreview" class="rounded border" :style="{ width: previewSize + 'px', height: previewSize + 'px', objectFit: 'cover' }" />
        </div>
    </div>
</template>

<style>
cropper-canvas {
    display: block;
    width: 100%;
    height: 100%;
}
</style>