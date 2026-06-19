package com.example.demo.controller;

import com.example.demo.model.Shop;
import com.example.demo.repository.ShopRepository;
import com.example.demo.service.ImageService;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopRepository shopRepository;
    private final ImageService imageService;

    public ShopController(ShopRepository shopRepository, ImageService imageService) {
        this.shopRepository = shopRepository;
        this.imageService = imageService;
    }

    @GetMapping
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return shopRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 画像配信
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Path path = imageService.resolve(filename);
        if (!path.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new PathResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    // 商品登録（画像あり）
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Shop> createShop(
            @RequestPart("name") String name,
            @RequestPart("price") String price,
            @RequestPart("stock") String stock,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (shopRepository.existsByName(name)) {
            return ResponseEntity.badRequest().build();
        }

        Shop shop = new Shop();
        shop.setName(name);
        shop.setPrice(new BigDecimal(price));
        shop.setStock(Integer.parseInt(stock));

        if (image != null && !image.isEmpty()) {
            shop.setImagePath(imageService.save(image));
        }

        return ResponseEntity.ok(shopRepository.save(shop));
    }

    // 商品更新（画像あり）
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Shop> updateShop(
            @PathVariable Long id,
            @RequestPart("name") String name,
            @RequestPart("price") String price,
            @RequestPart("stock") String stock,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return shopRepository.findById(id)
                .map(shop -> {
                    shop.setName(name);
                    shop.setPrice(new BigDecimal(price));
                    shop.setStock(Integer.parseInt(stock));

                    if (image != null && !image.isEmpty()) {
                        imageService.delete(shop.getImagePath());
                        try {
                            shop.setImagePath(imageService.save(image));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return ResponseEntity.ok(shopRepository.save(shop));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        return shopRepository.findById(id)
                .map(shop -> {
                    imageService.delete(shop.getImagePath());
                    shopRepository.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

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