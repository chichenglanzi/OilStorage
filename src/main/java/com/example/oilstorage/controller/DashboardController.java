package com.example.oilstorage.controller;

import com.example.oilstorage.config.CustomUserDetails;
import com.example.oilstorage.entity.SensorData;
import com.example.oilstorage.entity.User;
import com.example.oilstorage.service.DashboardService;
import com.example.oilstorage.service.SensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final SensorDataService sensorDataService;

    // 构造器注入（推荐）
    public DashboardController(DashboardService dashboardService, SensorDataService sensorDataService) {
        this.dashboardService = dashboardService;
        this.sensorDataService = sensorDataService;
    }

    @GetMapping("/sensor-data/{warehouseId}")
    @ResponseBody
    public List<SensorData> getSensorData(@PathVariable Integer warehouseId, Authentication authentication) {
        // 可以在这里添加权限检查逻辑，例如判断用户是否有权限访问该仓库的传感器数据
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
//        // 简单示例：如果用户角色不是admin或operator，不允许访问
//        if (!user.getRole().equals("admin") &&!user.getRole().equals("operator")) {
//            // 这里可以返回错误信息，比如空列表或特定的错误对象
//            return List.of();
//        }
        return sensorDataService.getLatestSensorData(warehouseId);
    }

    @GetMapping("/dashboard")
    public String showDashboard(
            Authentication authentication, // 自动注入已认证用户
            Model model
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();  // 获取实体对象
        // 添加用户名和角色到 Model
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole().name()); // 角色是枚举，使用 name() 获取字符串

        try {
            log.info("用户 {} 访问仪表盘，角色：{}", user.getUsername(), user.getRole());

            // 调用服务层获取数据
            model.addAllAttributes(dashboardService.getDashboardData(user));

            // 根据角色返回不同视图（可选）
            return  "dashboard";
        } catch (Exception e) {
            log.error("仪表盘数据加载失败", e);
            model.addAttribute("errorMessage", "系统繁忙，请稍后再试");
            return "error";
        }
    }
}