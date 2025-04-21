package com.example.oilstorage.repository;


import com.example.oilstorage.entity.BlockchainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransaction, String> {
    // 可选：自定义查询方法（根据业务需求添加）
    // 示例：按油库ID和交易类型查询
    java.util.List<BlockchainTransaction> findByWarehouseIdAndTxType(
            Integer warehouseId,
            BlockchainTransaction.TransactionType txType
    );
}
