package com.example.oilstorage.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data",
        indexes = {
                @Index(name = "idx_warehouse_time", columnList = "warehouse_id, timestamp"),
                @Index(name = "idx_alert_warehouse", columnList = "is_alert, warehouse_id")
        })
@Data
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidity;

    @Column(precision = 10, scale = 2)
    private BigDecimal stock;

    @Column(name = "is_alert")
    private Boolean isAlert;

    @Column(name = "tx_hash", length = 66)
    private String txHash;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;
}

