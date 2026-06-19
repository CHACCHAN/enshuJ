<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { shopApi } from "../../api/shop";
import { useCart } from "../../composables/useCart";
import type { Shop } from "../../types/shop";
import ProductCard from "./ProductCard.vue";
import CartSidebar from "./CartSidebar.vue";

const { items, totalItems, totalPrice, addItem, removeItem, updateQuantity, clearCart } = useCart();

const products = ref<Shop[]>([]);
const loading = ref(true);
const searchQuery = ref("");

const filteredProducts = computed(() => {
    if (!searchQuery.value) return products.value;
    const q = searchQuery.value.toLowerCase();
    return products.value.filter(p => p.name.toLowerCase().includes(q));
});

async function fetchProducts(): Promise<void> {
    loading.value = true;
    try {
        products.value = await shopApi.getAll();
    } finally {
        loading.value = false;
    }
}

async function handleCheckout(): Promise<void> {
    try {
        for (const item of items.value) {
            await shopApi.updateStock(item.product.id!, -item.quantity);
        }
        clearCart();
        alert("購入が完了しました");
        fetchProducts();
    } catch {
        alert("購入に失敗しました");
    }
}

onMounted(fetchProducts);
</script>

<template>
    <div class="min-vh-100" style="background: #EAEDED;">
        <!-- ヘッダー -->
        <nav class="navbar py-1 px-3" style="background: #232F3E;">
            <div class="container-fluid">
                <RouterLink to="/" class="navbar-brand text-white fw-medium">Demo Shop</RouterLink>
                <div class="d-flex align-items-center gap-3">
                    <div class="position-relative" style="width: 300px;">
                        <input
                            v-model="searchQuery"
                            type="text"
                            class="form-control form-control-sm rounded"
                            placeholder="商品を検索..."
                        />
                    </div>
                    <button
                        class="btn btn-sm text-white position-relative d-md-none"
                        type="button"
                        data-bs-toggle="offcanvas"
                        data-bs-target="#cartOffcanvas"
                    >
                        カート
                        <span v-if="totalItems > 0" class="badge rounded-pill" style="background: #F08804;">
                            {{ totalItems }}
                        </span>
                    </button>
                </div>
            </div>
        </nav>

        <!-- メインコンテンツ -->
        <div class="container-fluid py-3 px-3 px-md-4">
            <div class="row g-3">
                <!-- 商品一覧 -->
                <div class="col-12 col-md-9">
                    <h6 class="fw-medium mb-3">すべての商品</h6>
                    <div v-if="loading" class="text-center py-5 text-muted">読み込み中...</div>
                    <div v-else-if="filteredProducts.length === 0" class="text-center py-5 text-muted">
                        商品が見つかりません
                    </div>
                    <div v-else class="row row-cols-2 row-cols-sm-3 row-cols-lg-4 g-3">
                        <div v-for="product in filteredProducts" :key="product.id" class="col">
                            <ProductCard :product="product" @addToCart="addItem" />
                        </div>
                    </div>
                </div>

                <!-- カート（PC用サイドバー） -->
                <div class="col-md-3 d-none d-md-block">
                    <div class="position-sticky" style="top: 1rem;">
                        <CartSidebar
                        :items="items"
                        :totalItems="totalItems"
                        :totalPrice="totalPrice"
                        @updateQuantity="updateQuantity"
                        @removeItem="removeItem"
                        @checkout="handleCheckout"
                        />
                    </div>
                </div>
            </div>
        </div>

        <!-- カート（モバイル用オフキャンバス） -->
        <div class="offcanvas offcanvas-end" id="cartOffcanvas" tabindex="-1">
            <div class="offcanvas-header">
                <h6 class="offcanvas-title fw-medium">カート</h6>
                <button type="button" class="btn-close" data-bs-dismiss="offcanvas" />
            </div>
            <div class="offcanvas-body p-0">
                <div class="p-3">
                    <CartSidebar
                        :items="items"
                        :totalItems="totalItems"
                        :totalPrice="totalPrice"
                        @updateQuantity="updateQuantity"
                        @removeItem="removeItem"
                        @checkout="handleCheckout"
                    />
                </div>
            </div>
        </div>
    </div>
</template>