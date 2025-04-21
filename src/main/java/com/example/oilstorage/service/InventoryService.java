package com.example.oilstorage.service;

import com.example.oilstorage.config.CustomUserDetails;
import com.example.oilstorage.entity.BlockchainTransaction;
import com.example.oilstorage.entity.User;
import com.example.oilstorage.entity.Warehouse;
import com.example.oilstorage.repository.BlockchainTransactionRepository;
import com.example.oilstorage.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional // 保证库存更新与交易记录的事务一致性
public class InventoryService {

    private final WarehouseRepository warehouseRepo;
    private final BlockchainTransactionRepository transactionRepo;

    /**
     * 库存入库操作
     * @param warehouseId 油库ID
     * @param amount 入库数量（吨，必须为正数）
     * @param authentication Spring Security认证对象
     */
    public void stockIn(Integer warehouseId, BigDecimal amount, Authentication authentication) {
        executeStockOperation(warehouseId, amount, authentication, true);
    }

    /**
     * 库存出库操作
     * @param warehouseId 油库ID
     * @param amount 出库数量（吨，必须为正数且不超过当前库存）
     * @param authentication Spring Security认证对象
     */
    public void stockOut(Integer warehouseId, BigDecimal amount, Authentication authentication) {
        executeStockOperation(warehouseId, amount, authentication, false);
    }

    private void executeStockOperation(
            Integer warehouseId,
            BigDecimal amount,
            Authentication authentication,
            boolean isInOperation
    ) {
        // 1. 校验用户认证和操作数量
        validateAuthentication(authentication);
        validateOperationAmount(amount, isInOperation);

        // 2. 获取并校验油库信息
        Warehouse warehouse = getValidWarehouse(warehouseId);
        validateStockAvailability(warehouse, amount, isInOperation);

        // 3. 执行库存变更
        updateWarehouseStock(warehouse, amount, isInOperation);

        // 4. 创建区块链交易记录
        createBlockchainTransaction(warehouse, amount, authentication, isInOperation);
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("用户未认证或会话已过期");
        }
    }

    private void validateOperationAmount(BigDecimal amount, boolean isInOperation) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("操作数量必须为正数（例如：10.5）");
        }
        if (!isInOperation && amount.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("出库数量不能小于1吨");
        }
        // 限制最多两位小数
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("数量最多支持两位小数（例如：10.00）");
        }
    }

    private Warehouse getValidWarehouse(Integer warehouseId) {
        return warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("油库不存在，ID：" + warehouseId));
    }

    private void validateStockAvailability(
            Warehouse warehouse,
            BigDecimal amount,
            boolean isInOperation
    ) {
        if (!isInOperation && warehouse.getStock().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "库存不足，当前库存：" + warehouse.getStock() + " 吨，所需出库量：" + amount + " 吨"
            );
        }
    }

    private void updateWarehouseStock(
            Warehouse warehouse,
            BigDecimal amount,
            boolean isInOperation
    ) {
        BigDecimal newStock = isInOperation ?
                warehouse.getStock().add(amount) :
                warehouse.getStock().subtract(amount);

        // 保留两位小数，四舍五入
        newStock = newStock.setScale(2, RoundingMode.HALF_UP);
        warehouse.setStock(newStock);
        warehouseRepo.save(warehouse);
    }

    private void createBlockchainTransaction(
            Warehouse warehouse,
            BigDecimal amount,
            Authentication authentication,
            boolean isInOperation
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        BlockchainTransaction transaction = new BlockchainTransaction();
        transaction.setTxHash(UUID.randomUUID().toString()); // 生成唯一交易ID
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTxType(isInOperation ?
                BlockchainTransaction.TransactionType.STOCK_IN :
                BlockchainTransaction.TransactionType.STOCK_OUT);
        transaction.setStockChange(isInOperation ? amount : amount.negate()); // 入库为正，出库为负
        transaction.setOperatorAddress(currentUser.getBlockchainAddress());
        transaction.setWarehouse(warehouse); // 关联油库实体

        transactionRepo.save(transaction);
    }
}