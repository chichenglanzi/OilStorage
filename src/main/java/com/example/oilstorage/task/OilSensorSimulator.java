package com.example.oilstorage.task;

import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.entity.Warehouse;
import com.example.oilstorage.repository.SensorDataRepository;
import com.example.oilstorage.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.*;

@Component
public class OilSensorSimulator {
    private static final Logger log = LoggerFactory.getLogger(OilSensorSimulator.class);

    // 阈值配置（网页8）
    private static final BigDecimal TEMP_ALERT = new BigDecimal("40.00");
    private static final BigDecimal HUMIDITY_ALERT = new BigDecimal("85.00");
    private static final BigDecimal MIN_STOCK = new BigDecimal("5000.00");
    private static final BigDecimal MAX_STOCK = new BigDecimal("20000.00");

    // 数据连续性增强参数（网页1/网页8）
    private static final int HISTORY_SIZE = 3; // 历史数据窗口大小
    private static final BigDecimal TEMP_VARIATION = new BigDecimal("0.02"); // 2%温度波动
    private static final BigDecimal HUMIDITY_VARIATION = new BigDecimal("0.03"); // 3%湿度波动

    // 状态存储（线程安全）
    private final ConcurrentHashMap<Integer, Deque<BigDecimal>> tempHistory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Deque<BigDecimal>> humidityHistory = new ConcurrentHashMap<>();

    private final WarehouseRepository warehouseRepo;
    private final SensorDataRepository sensorDataRepo;

    public OilSensorSimulator(WarehouseRepository warehouseRepo, SensorDataRepository sensorDataRepo) {
        this.warehouseRepo = warehouseRepo;
        this.sensorDataRepo = sensorDataRepo;
    }

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void generateData() {
        warehouseRepo.findAll().parallelStream().forEach(warehouse -> {
            try {
                initializeStockIfNeeded(warehouse);
                SensorData data = generateStableSensorData(warehouse);
                checkAndHandleAlert(data);
                sensorDataRepo.save(data);
            } catch (Exception e) {
                log.error("仓库{}数据处理异常: {}", warehouse.getId(), e.getMessage());
            }
        });
    }

    private void initializeStockIfNeeded(Warehouse warehouse) {
        if (warehouse.getStock() == null) {
            BigDecimal stock = generateRandomValue(MIN_STOCK, MAX_STOCK);
            warehouse.setStock(stock);
            warehouseRepo.save(warehouse);
            log.info("初始化仓库 {} 库存: {} 吨", warehouse.getName(), stock);
        }
    }

    private SensorData generateStableSensorData(Warehouse warehouse) {
        Integer warehouseId = warehouse.getId();
        SensorData data = new SensorData();
        data.setWarehouse(warehouse);
        data.setTimestamp(LocalDateTime.now());

        // 温度生成（带历史平滑）
        data.setTemperature(generateStableValue(
                warehouseId,
                tempHistory,
                new BigDecimal("-20.00"),
                new BigDecimal("39.99"),
                TEMP_VARIATION
        ));

        // 湿度生成（带历史平滑）
        data.setHumidity(generateStableValue(
                warehouseId,
                humidityHistory,
                BigDecimal.ZERO,
                new BigDecimal("84.99"),
                HUMIDITY_VARIATION
        ));

        data.setStock(warehouse.getStock());
        return data;
    }

    private BigDecimal generateStableValue(Integer warehouseId,
                                           ConcurrentHashMap<Integer, Deque<BigDecimal>> historyMap,
                                           BigDecimal min,
                                           BigDecimal max,
                                           BigDecimal maxChange) {
        Deque<BigDecimal> history = historyMap.computeIfAbsent(warehouseId, k -> new ConcurrentLinkedDeque<>());

        // 计算滑动平均值（网页1/网页8）
        BigDecimal baseValue = history.isEmpty() ?
                generateRandomValue(min, max) :
                history.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(history.size()), 2, RoundingMode.HALF_UP);

        // 动态波动范围（网页8）
        BigDecimal minVal = baseValue.multiply(BigDecimal.ONE.subtract(maxChange)).max(min);
        BigDecimal maxVal = baseValue.multiply(BigDecimal.ONE.add(maxChange)).min(max);

        BigDecimal newValue = generateRandomValue(minVal, maxVal);

        // 更新历史队列
        if (history.size() >= HISTORY_SIZE) {
            history.pollFirst();
        }
        history.offerLast(newValue);

        return newValue;
    }

    private void checkAndHandleAlert(SensorData data) {
        try {
            boolean tempAlert = data.getTemperature().compareTo(TEMP_ALERT) > 0;
            boolean humidityAlert = data.getHumidity().compareTo(HUMIDITY_ALERT) > 0;
            boolean stockAlert = data.getStock().compareTo(MIN_STOCK) < 0;

            data.setIsAlert(tempAlert || humidityAlert || stockAlert);

            // 告警时允许突变，非告警保持连续性（网页8）
            if (!data.getIsAlert()) {
                updateHistory(data);
            }

            if (data.getIsAlert()) {
                log.warn("仓库 {} 告警 - 温度:{} 湿度:{} 库存:{}",
                        data.getWarehouse().getName(),
                        tempAlert ? "过高" : "正常",
                        humidityAlert ? "过高" : "正常",
                        stockAlert ? "不足" : "充足");
            }
        } catch (Exception e) {
            log.error("告警检测异常: {}", e.getMessage());
        }
    }

    private void updateHistory(SensorData data) {
        Integer warehouseId = data.getWarehouse().getId();
        tempHistory.get(warehouseId).offerLast(data.getTemperature());
        humidityHistory.get(warehouseId).offerLast(data.getHumidity());
    }

    private BigDecimal generateRandomValue(BigDecimal min, BigDecimal max) {
        double range = max.subtract(min).doubleValue();
        return min.add(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(range)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}