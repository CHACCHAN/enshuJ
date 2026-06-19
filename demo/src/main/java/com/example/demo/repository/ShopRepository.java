package com.example.demo.repository;

import com.example.demo.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 商品名で検索
    Optional<Shop> findByName(String name);

    // 在庫が指定数より多い商品
    List<Shop> findByStockGreaterThan(int stock);

    // 価格帯で絞り込み
    List<Shop> findByPriceBetween(BigDecimal min, BigDecimal max);

    // 商品名の重複チェック
    boolean existsByName(String name);
}