package com.example.oilstorage.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "blockchain_transactions",
        indexes = {
                @Index(name = "idx_operator", columnList = "operator_address, warehouse_id"),
                @Index(name = "idx_tx_type", columnList = "tx_type")
        }
)
// 添加注解（若该实体被直接序列化）
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class BlockchainTransaction {

    // 主键（自增ID，数据库生成）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 关联传感器数据（多对一，外键：data_id，关联SensorData的id）
    @ManyToOne
    @JoinColumn(
            name = "data_id",          // 数据库外键列名
            referencedColumnName = "id",  // 引用SensorData的主键
            nullable = false            // 非空约束（根据业务需求）
    )
    private SensorData sensorData;

    // 关联油库（多对一，外键：warehouse_id，关联Warehouse的id）
    @ManyToOne
    @JoinColumn(
            name = "warehouse_id",       // 数据库外键列名
            referencedColumnName = "id",  // 引用Warehouse的主键
            nullable = false            // 非空约束（必须关联有效油库）
    )
    private Warehouse warehouse;

    // 交易哈希（唯一标识，可作为业务唯一键）
    @Column(name = "tx_hash", length = 66, unique = true)
    private String txHash;

    // 区块高度
    @Column(name = "block_number")
    private Long blockNumber;

    // 交易时间（非空）
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // 交易类型（枚举：库存变更/告警事件）
    @Enumerated(EnumType.STRING)
    @Column(name = "tx_type", nullable = false)
    private TransactionType txType;

    // 操作地址（区块链地址，长度42）
    @Column(name = "operator_address", length = 42)
    private String operatorAddress;

    // 库存变化量（精确到2位小数，正数为入库，负数为出库）
    @Column(name = "stock_change", precision = 10, scale = 2)
    private BigDecimal stockChange;

    // 交易类型枚举
    public enum TransactionType {
        STOCK_CHANGE,   // 库存变更（对应stock_change有值）
        ALERT_EVENT     // 告警事件（对应sensorData的is_alert为true）
    }
}