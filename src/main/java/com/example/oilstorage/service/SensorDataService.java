package com.example.oilstorage.service;

import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // 添加 Lombok 日志注解，生成 log 变量
public class SensorDataService {
    private final SensorDataRepository sensorDataRepo;

    @Transactional(readOnly = true)
    public List<SensorData> getLatestSensorData(Integer warehouseId) {
        // 移除未定义的 result 变量，直接返回查询结果
        List<SensorData> sensorDataList = sensorDataRepo.findTop10ByWarehouseIdOrderByTimestampDesc(warehouseId);

        // 正确的日志输出（使用 sensorDataList 而非未定义的 result）
        log.info("查询油库 {} 的传感器数据，返回 {} 条", warehouseId, sensorDataList.size());

        return sensorDataList;
    }
}