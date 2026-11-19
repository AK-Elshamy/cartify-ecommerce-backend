package com.elshamy.cartify.service.auth;

import com.elshamy.cartify.dtos.AuthDTO;

public interface IAuth {
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);

    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);
}