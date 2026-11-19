package com.elshamy.cartify.service.auth;

import com.elshamy.cartify.dtos.AuthDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.model.UserRole;
import com.elshamy.cartify.repository.UserRepository;
import com.elshamy.cartify.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthImpl implements IAuth {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered: " + request.email());
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthDTO.AuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    @Override
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );


        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new AuthDTO.AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}