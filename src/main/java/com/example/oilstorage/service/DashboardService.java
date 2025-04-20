package com.example.oilstorage.service;

import com.example.oilstorage.entity.User;
import com.example.oilstorage.entity.Warehouse;
import com.example.oilstorage.repository.DashboardRepository;
import com.example.oilstorage.repository.UserRepository;
import com.example.oilstorage.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepo;
    private final WarehouseRepository warehouseRepo;
    private final SensorDataService sensorDataService;
    private final UserRepository userRepository;

    public Map<String, Object> getDashboardData(User user) {
        // 防御性检查：确保 user 不为 null
        Assert.notNull(user, "User must not be null");
        // 确保角色不为 null（因为枚举不会是 null，但可以检查是否有值）
        Assert.notNull(user.getRole(), "User role must not be null");

        Map<String, Object> data = new HashMap<>();

        // 获取油库列表（保持原有逻辑不变）
        List<Warehouse> warehouses = switch (user.getRole()) {
            case admin, operator -> dashboardRepo.findUserWarehouses(user.getId());
            case auditor -> warehouseRepo.findAll();
            default -> throw new IllegalArgumentException("Unsupported role: " + user.getRole());
        };

        // 获取第一个油库的传感器（保持原有逻辑不变）
        if (!warehouses.isEmpty()) {
            Warehouse first = warehouses.get(0);
            first.setSensorData(sensorDataService.getLatestSensorData(first.getId()));
        }

        return Map.of("warehouses", warehouses);
    }
}

