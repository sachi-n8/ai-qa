package com.psi.ai_qa.interfaces.auth.service.impl;

import com.psi.ai_qa.common.model.User;
import com.psi.ai_qa.common.repo.UserRepository;
import com.psi.ai_qa.common.util.JwtUtil;
import com.psi.ai_qa.interfaces.auth.service.AuthSerivce;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class AuthServiceImpl implements AuthSerivce {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String authenticate(User user) {

        User userDetail = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(userDetail);
    }
}
