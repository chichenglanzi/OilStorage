package com.example.oilstorage.service;

import com.example.oilstorage.config.CustomUserDetails;
import com.example.oilstorage.entity.User;
import com.example.oilstorage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("尝试加载用户：{}", username);
        try {
            User user = userRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("用户未找到: " + username));
            logger.info("成功加载用户：{}", user.getUsername());
//            return org.springframework.security.core.userdetails.User
//                    .withUsername(user.getUsername())
//                    .password(user.getPasswordHash())
//                    .roles(user.getRole().name())
//                    .build();
            return new CustomUserDetails(user);
        } catch (Exception e) {
            logger.error("未知异常: {}", e.toString(), e);
            throw new UsernameNotFoundException("系统异常", e);
        }
    }
}