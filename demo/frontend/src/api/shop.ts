import api from "../lib/api";
import type { Shop } from "../types/shop";

export const shopApi = {
    getAll(): Promise<Shop[]> {
        return api.get<Shop[]>('/shop').then(r => r.data)
    },

    getById(id: number): Promise<Shop> {
        return api.get<Shop>(`/shop/${id}`).then(r => r.data)
    },

    create(shop: Omit<Shop, 'id'>, image?: File): Promise<Shop> {
        const fd = new FormData()
        fd.append('name', shop.name)
        fd.append('price', String(shop.price))
        fd.append('stock', String(shop.stock))
        if (image) fd.append('image', image)
        return api.post<Shop>('/shop', fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
        }).then(r => r.data)
    },

    update(id: number, shop: Shop, image?: File): Promise<Shop> {
        const fd = new FormData()
        fd.append('name', shop.name)
        fd.append('price', String(shop.price))
        fd.append('stock', String(shop.stock))
        if (image) fd.append('image', image)
        return api.put<Shop>(`/shop/${id}`, fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
        }).then(r => r.data)
    },

    remove(id: number): Promise<void> {
        return api.delete(`/shop/${id}`).then(() => undefined)
    },

    updateStock(id: number, quantity: number): Promise<Shop> {
        return api.patch<Shop>(`/shop/${id}/stock`, quantity).then(r => r.data)
    },

    imageUrl(imagePath: string): string {
        return `/api/shop/images/${imagePath}`
    },
}