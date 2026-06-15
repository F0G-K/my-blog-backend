package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.common.ResultCode;
import org.example.myblogbackend.dto.LoginRequest;
import org.example.myblogbackend.dto.LoginVO;
import org.example.myblogbackend.entity.Admin;
import org.example.myblogbackend.mapper.AdminMapper;
import org.example.myblogbackend.security.JwtUtil;
import org.example.myblogbackend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginRequest request) {
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>().eq("username", request.getUsername()));
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        return new LoginVO(jwtUtil.generate(admin.getUsername()));
    }
}
