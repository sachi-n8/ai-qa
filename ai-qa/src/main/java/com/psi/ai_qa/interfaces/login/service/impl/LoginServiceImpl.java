package com.psi.ai_qa.interfaces.login.service.impl;


import com.psi.ai_qa.common.constants.StatusAndMessage;
import com.psi.ai_qa.common.enums.ResponseStatusAndMessage;
import com.psi.ai_qa.common.exception.WalletException;
import com.psi.ai_qa.common.model.User;
import com.psi.ai_qa.common.repo.UserRepository;
import com.psi.ai_qa.interfaces.auth.service.AuthSerivce;
import com.psi.ai_qa.interfaces.login.model.UserRequest;
import com.psi.ai_qa.interfaces.login.model.UserResponse;
import com.psi.ai_qa.interfaces.login.service.LoginService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthSerivce authSerivce;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserResponse login(UserRequest userRequest) {

        log.info("Login attempt for email: {}", userRequest.getEmail());

        Optional<User> userDetail = userRepository.findByEmail(userRequest.getEmail());
        User user;

        if (userDetail.isEmpty()) {
            log.info("User not found. Creating new user with email: {}", userRequest.getEmail());

            user = new User();
            user.setUserId(generateUniqueUserId());
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userRepository.save(user);

            log.info("New user created with userId: {}", user.getUserId());


        } else {

            user = userDetail.get();
            log.info("Existing user found with userId: {}", user.getUserId());

            if (!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
                log.warn("Password mismatch for userId: {}", user.getUserId());
                throw new WalletException(ResponseStatusAndMessage.PASSWORD_DOES_NOT_MATCH);
            }

            log.info("Password validation successful for userId: {}", user.getEmail());
        }

        log.info("Generating JWT token for userId: {}", user.getUserId());
        String token = authSerivce.authenticate(user);

        log.info("Login successful for userId: {}", user.getUserId());

        UserResponse userResponse = new UserResponse();
        userResponse.setStatusCode(StatusAndMessage.SUCCESS);
        userResponse.setStatusMessage(StatusAndMessage.USER_LOGGED_IN);
        userResponse.setUserID(user.getUserId());
        userResponse.setJwtToken(token);

        return userResponse;
    }

    private String generateUniqueUserId() {
        String userId;
        do {
            userId = UUID.randomUUID().toString();
        } while (userRepository.existsUserByUserId(userId));

        log.debug("Generated new unique userId: {}", userId);
        return userId;
    }
}
