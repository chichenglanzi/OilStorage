package com.example.oilstorage.controller;

import com.example.oilstorage.config.CustomUserDetails;
import com.example.oilstorage.entity.BlockchainTransaction;
import com.example.oilstorage.entity.User;
import com.example.oilstorage.entity.Warehouse;
import com.example.oilstorage.repository.BlockchainTransactionRepository;
import com.example.oilstorage.repository.WarehouseRepository;
import com.example.oilstorage.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final WarehouseRepository warehouseRepo;
    private final BlockchainTransactionRepository transactionRepo;

    // 入库操作
    @PostMapping("/warehouse/{warehouseId}/in")
    public ResponseEntity<?> stockIn(
            @PathVariable Integer warehouseId,
            @RequestParam("amount") BigDecimal amount,
            Authentication authentication
    ) {
        try {
            inventoryService.stockIn(warehouseId, amount, authentication);
            return ResponseEntity.ok("入库操作成功");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 出库操作
    @PostMapping("/warehouse/{warehouseId}/out")
    public ResponseEntity<?> stockOut(
            @PathVariable Integer warehouseId,
            @RequestParam("amount") BigDecimal amount,
            Authentication authentication
    ) {
        try {
            inventoryService.stockOut(warehouseId, amount, authentication);
            return ResponseEntity.ok("出库操作成功");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 新增：获取油库的前10条区块链交易记录
    @GetMapping("/warehouse/{warehouseId}/transactions")
    public ResponseEntity<List<BlockchainTransaction>> getBlockchainTransactions(
            @PathVariable Integer warehouseId
    ) {
        try {
            List<BlockchainTransaction> transactions = inventoryService.getLatestBlockchainTransactions(warehouseId);
            return ResponseEntity.ok(transactions);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}