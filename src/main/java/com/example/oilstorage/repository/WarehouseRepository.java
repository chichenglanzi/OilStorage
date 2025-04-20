package com.example.oilstorage.repository;

import com.example.oilstorage.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository // 可省略（Spring Data JPA自动注册）
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    // 自定义查询方法示例（按名称查找）
    Warehouse findByName(String name);

    // 自定义更新方法（库存量调整）
    @Modifying
    @Query("UPDATE Warehouse w SET w.stock = :newStock WHERE w.id = :id")
    void updateStock(@Param("id") Integer id, @Param("newStock") BigDecimal newStock);
}
