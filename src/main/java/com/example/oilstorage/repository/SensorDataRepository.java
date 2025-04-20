package com.example.oilstorage.repository;

import com.example.oilstorage.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JPA Repository接口
public interface SensorDataRepository extends JpaRepository<SensorData, Integer> {
    List<SensorData> findTop10ByWarehouseIdOrderByTimestampDesc(Integer warehouseId);
}
