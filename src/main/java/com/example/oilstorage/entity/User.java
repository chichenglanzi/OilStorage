package com.example.oilstorage.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

// entity/User.java
@Data
@Entity
@Table(name = "users")
// 添加注解，使用实体的id作为唯一标识
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 64) // varchar(64)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('admin','operator','auditor') DEFAULT 'operator'")
    private Role role;

    @Column(name = "blockchain_address", unique = true, length = 42)
    private String blockchainAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Role { admin, operator, auditor }


    // 反向关联关系
    @OneToMany(mappedBy = "user")
    private List<UserWarehouseRelation> warehouseRelations;
    // 一对多关联，仅适用于 admin 角色
    @OneToMany(mappedBy = "manager")
    private List<Warehouse> managedWarehouses;
}