package com.example.oilstorage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "user_warehouse_relations",
        indexes = {
                @Index(name = "idx_user_warehouse", columnList = "user_id, warehouse_id"),
                @Index(name = "idx_warehouse_user", columnList = "warehouse_id, user_id")
        })

public class UserWarehouseRelation {
    @EmbeddedId
    private UserWarehouseId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"  // 明确引用 User 表的主键列
    )
    private User user;
    @ManyToOne
    @MapsId("warehouseId")
    @JoinColumn(
            name = "warehouse_id",
            referencedColumnName = "id"  // 引用 Warehouse 表的主键列
    )
    private Warehouse warehouse;

    @Data
    @Embeddable
    public static class UserWarehouseId implements Serializable {
        private Integer userId;
        private Integer warehouseId;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserWarehouseId that = (UserWarehouseId) o;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(warehouseId, that.warehouseId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, warehouseId);
        }

    }

}