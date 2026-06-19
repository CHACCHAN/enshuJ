package com.example.demo.controller;

import com.example.demo.model.Shop;
import com.example.demo.repository.ShopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopRepository shopRepository;

    public ShopController(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // 全商品一覧
    @GetMapping
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    // 1件取得
    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return shopRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 商品登録
    @PostMapping
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
        if (shopRepository.existsByName(shop.getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(shopRepository.save(shop));
    }

    // 商品更新
    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @RequestBody Shop updated) {
        return shopRepository.findById(id)
                .map(shop -> {
                    shop.setName(updated.getName());
                    shop.setPrice(updated.getPrice());
                    shop.setStock(updated.getStock());
                    return ResponseEntity.ok(shopRepository.save(shop));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 商品削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        if (!shopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        shopRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 在庫数のみ更新
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Shop> updateStock(@PathVariable Long id, @RequestBody int quantity) {
        return shopRepository.findById(id)
                .map(shop -> {
                    shop.setStock(shop.getStock() + quantity);
                    return ResponseEntity.ok(shopRepository.save(shop));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}