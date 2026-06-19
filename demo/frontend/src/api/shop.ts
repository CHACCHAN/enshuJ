import api from "../lib/api";
import type { Shop } from "../types/shop";

export const shopApi = {
    getAll(): Promise<Shop[]> {
        return api.get<Shop[]>("/shop").then(r => r.data)
    },

    getById(id: number): Promise<Shop> {
        return api.get<Shop>(`/shop/${id}`).then(r => r.data)
    },

    create(shop: Omit<Shop, "id">): Promise<Shop> {
        return api.post<Shop>("/shop", shop).then(r => r.data)
    },

    update(id: number, shop: Shop): Promise<Shop> {
        return api.put<Shop>(`/shop/${id}`, shop).then(r => r.data)
    },

    remove(id: number): Promise<void> {
        return api.delete(`/shop/${id}`).then(() => undefined)
    },

    updateStock(id: number, quantity: number): Promise<Shop> {
        return api.patch<Shop>(`/shop/${id}/stock`, quantity).then(r => r.data)
    },
}