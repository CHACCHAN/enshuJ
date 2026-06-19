export interface Shop {
    id?: number;
    name: string;
    price: number;
    stock: number;
    imagePath?: string | null;
}