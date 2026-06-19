import type { Shop } from "./shop";

export interface CartItem {
    product: Shop;
    quantity: number;
}