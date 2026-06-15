package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.LoginRequest;
import org.example.myblogbackend.dto.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest request);
}
