pragma solidity ^0.4.25;

contract OilStorage {
    // 库存变更记录
    struct StockChange {
        uint warehouseId;
        int amount;
        uint timestamp;
        address operator;
    }

    // 环境告警事件
    struct AlertEvent {
        uint warehouseId;
        uint temperature;
        uint humidity;
        uint timestamp;
    }

    mapping(uint => int) public stockBalances; // 仓库当前库存
    StockChange[] public stockChanges;         // 库存变更历史
    AlertEvent[] public alertEvents;           // 告警事件记录

    // 事件定义
    event StockChanged(uint indexed warehouseId, int amount, address operator);
    event AlertTriggered(uint indexed warehouseId, uint temperature, uint humidity);

    // 添加库存变更记录（需处理为实际业务逻辑）
    function addStockChange(uint _warehouseId, int _amount) public {
        require(_warehouseId > 0, "Invalid warehouse ID");
        
        stockBalances[_warehouseId] += _amount;
        stockChanges.push(StockChange({
            warehouseId: _warehouseId,
            amount: _amount,
            timestamp: now,
            operator: msg.sender
        }));
        
        emit StockChanged(_warehouseId, _amount, msg.sender);
    }

    // 记录环境告警事件（需由链下系统触发）
    function logAlertEvent(
        uint _warehouseId,
        uint _temperature,
        uint _humidity
    ) public {
        require(_warehouseId > 0, "Invalid warehouse ID");
        
        alertEvents.push(AlertEvent({
            warehouseId: _warehouseId,
            temperature: _temperature,
            humidity: _humidity,
            timestamp: now
        }));
        
        emit AlertTriggered(_warehouseId, _temperature, _humidity);
    }

    // 获取库存余额
    function getStockBalance(uint _warehouseId) public view returns (int) {
        return stockBalances[_warehouseId];
    }

    // 获取库存变更记录数量
    function getStockChangesCount() public view returns (uint) {
        return stockChanges.length;
    }

    // 获取告警事件数量
    function getAlertEventsCount() public view returns (uint) {
        return alertEvents.length;
    }
}