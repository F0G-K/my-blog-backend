package org.example.myblogbackend.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.myblogbackend.entity.Admin;
import org.example.myblogbackend.mapper.AdminMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 启动时确保管理员账号存在:库中无该用户名则用 BCrypt 写入。
 * 不开放注册,单管理员。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final AdminProperties adminProperties;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String username = adminProperties.getUsername();
        String password = adminProperties.getPassword();
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("未配置 app.admin.username/password,跳过管理员初始化(无法登录)");
            return;
        }
        Admin existing = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", username));
        if (existing == null) {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPasswordHash(passwordEncoder.encode(password));
            adminMapper.insert(admin);
            log.info("已创建管理员账号: {}", username);
            return;
        }
        // 已存在(可能是建表脚本里的占位哈希):若配置密码无法验证,则同步为配置密码
        if (!passwordEncoder.matches(password, existing.getPasswordHash())) {
            existing.setPasswordHash(passwordEncoder.encode(password));
            adminMapper.updateById(existing);
            log.info("已将管理员 {} 的密码同步为配置值", username);
        }
    }
}
