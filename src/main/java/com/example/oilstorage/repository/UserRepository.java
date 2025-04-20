package com.example.oilstorage.repository;

import com.example.oilstorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // 根据用户名查询用户（方法名必须严格遵循规范）
    Optional<User> findByUsername(String username);


    // 检查区块链地址是否存在（可选）
    boolean existsByBlockchainAddress(String blockchainAddress);
}
