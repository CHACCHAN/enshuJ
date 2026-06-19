import { ref, computed } from "vue";
import type { Shop } from "../types/shop";
import type { CartItem } from "../types/cart";

const items = ref<CartItem[]>([]);

export function useCart() {
    const totalItems = computed(() =>
        items.value.reduce((sum, item) => sum + item.quantity, 0)
    );

    const totalPrice = computed(() =>
        items.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0)
    );

    function addItem(product: Shop): void {
        const existing = items.value.find(i => i.product.id === product.id)
        if (existing) {
            if (existing.quantity < product.stock) {
                existing.quantity++;
            }
        } else {
            items.value.push({ product, quantity: 1 });
        }
    }

    function removeItem(productId: number): void {
        items.value = items.value.filter(i => i.product.id !== productId);
    }

    function updateQuantity(productId: number, quantity: number): void {
        const item = items.value.find(i => i.product.id === productId);
        if (!item) return;
        if (quantity <= 0) {
            removeItem(productId);

        } else if (quantity <= item.product.stock) {
            item.quantity = quantity;
        }
    }

    function clearCart(): void {
        items.value = [];
    }

    return { items, totalItems, totalPrice, addItem, removeItem, updateQuantity, clearCart }
}