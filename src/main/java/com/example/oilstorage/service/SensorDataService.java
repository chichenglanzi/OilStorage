package com.example.oilstorage.service;

import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataRepository sensorDataRepo;

    @Transactional(readOnly = true)
    public List<SensorData> getLatestSensorData(Integer warehouseId) {
        return sensorDataRepo.findTop10ByWarehouseIdOrderByTimestampDesc(warehouseId);
    }
}
