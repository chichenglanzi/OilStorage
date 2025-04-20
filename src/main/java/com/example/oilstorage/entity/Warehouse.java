package com.example.oilstorage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "warehouses",
        indexes = @Index(name = "idx_manager", columnList = "manager_id"))

public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(precision = 10, scale = 2)
    @ColumnDefault("100.00") // 数据库默认值
    private BigDecimal stock;

//    @ManyToOne
//    @JoinColumn(name = "manager_id")
//    private User manager;

    @Column(name = "blockchain_id", unique = true)
    private Integer blockchainId;

    @Column(name = "blockchain_tx_hash", length = 66)
    private String blockchainTxHash;

    @Column(name = "blockchain_time")
    private LocalDateTime blockchainTime;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 双向关联
    @OneToMany(mappedBy = "warehouse")
    private List<SensorData> sensorData;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<UserWarehouseRelation> userWarehouseRelation;

    // Warehouse.java
    @ManyToOne
    @JoinColumn(
            name = "manager_id",         // 数据库外键列名
            referencedColumnName = "id"  // 引用 users 表的主键列
    )
    private User manager;            // 必须关联到 User 实体
}
