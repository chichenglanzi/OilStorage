package com.example.oilstorage.controller;

import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.entity.Warehouse;
import com.example.oilstorage.repository.SensorDataRepository;
import com.example.oilstorage.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ManualAlertController {

    private final WarehouseRepository warehouseRepo;
    private final SensorDataRepository sensorDataRepo;

    public ManualAlertController(WarehouseRepository warehouseRepo, SensorDataRepository sensorDataRepo) {
        this.warehouseRepo = warehouseRepo;
        this.sensorDataRepo = sensorDataRepo;
    }

    @GetMapping("/manual-alert")
    public String showManualAlertPage(Model model) {
        // 获取所有油库用于下拉菜单
        List<Warehouse> warehouses = warehouseRepo.findAll();
        model.addAttribute("warehouses", warehouses);
        return "manual_alert";
    }

    @PostMapping("/manual-alert")
    public String handleManualAlert(Integer warehouseId) {
        // 1. 校验油库是否存在（避免无效 ID 导致外键错误）
        if (!warehouseRepo.existsById(warehouseId)) {
            throw new EntityNotFoundException("油库不存在，ID：" + warehouseId);
        }

        // 2. 创建 SensorData 对象并设置关联的 Warehouse（通过 ID 关联）
        SensorData sensorData = new SensorData();

        // 直接创建 Warehouse 对象并设置 ID（JPA 允许通过 ID 关联，无需加载完整实体）
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(warehouseId); // 仅设置 ID，无需其他字段（@ManyToOne 支持 ID 关联）
//        sensorData.setWarehouse(warehouse); // 建立关联
// 从数据库查询完整的 Warehouse 实体
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("油库不存在，ID：" + warehouseId));
        sensorData.setWarehouse(warehouse); // 设置完整实体
        // 3. 设置传感器数据（温度 50℃ 触发告警，其他字段为合理默认值）
        sensorData.setTemperature(new BigDecimal("50.00"));
        sensorData.setHumidity(new BigDecimal("70.00")); // 正常湿度
        sensorData.setStock(new BigDecimal("10000.00")); // 正常库存
        sensorData.setIsAlert(true); // 强制触发告警
        sensorData.setTimestamp(LocalDateTime.now());

        // 4. 保存数据
        sensorDataRepo.save(sensorData);

        return "redirect:/manual-alert?success=true";
    }
}