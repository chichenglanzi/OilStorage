package com.example.oilstorage.config;

import com.example.oilstorage.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final User user;  // 核心：包裹实体对象

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 将实体属性映射到安全框架
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + user.getRole().name());  // 动态生成角色权限
    }

    // 暴露实体对象（关键扩展点）
    public User getUser() {
        return user;
    }

    // 以下方法根据业务需求实现（示例为全开放）
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}