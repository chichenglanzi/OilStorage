package com.example.oilstorage.repository;

import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// repository/DashboardRepository.java
public interface DashboardRepository extends JpaRepository<Warehouse, Integer> {

    // 用户关联油库查询
    @Query("SELECT w FROM Warehouse w JOIN w.userWarehouseRelation r WHERE r.user.id = ?1")
    List<Warehouse> findUserWarehouses(Integer userId);

    // 油库传感器查询
    @Query("SELECT s FROM SensorData s WHERE s.warehouse.id = ?1")
    List<SensorData> findSensorsByWarehouseId(Integer warehouseId);
}