package com.example.oilstorage.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blockchain_transactions",
        indexes = {
                @Index(name = "idx_operator", columnList = "operator_address, warehouse_id"),
                @Index(name = "idx_tx_type", columnList = "tx_type")
        })
public class BlockchainTransaction {
    @Id
    @Column(name = "tx_hash", length = 66)
    private String txHash;

    @ManyToOne
    @JoinColumn(name = "data_id")
    private SensorData sensorData;

    @Column(name = "block_number")
    private Long blockNumber;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "tx_type", nullable = false)
    private TransactionType txType;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "operator_address", length = 42)
    private String operatorAddress;

    public enum TransactionType { STOCK_CHANGE, ALERT_EVENT }
}
