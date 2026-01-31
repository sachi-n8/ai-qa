package com.psi.ai_qa.interfaces.login;


import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.interfaces.login.model.UserRequest;
import com.psi.ai_qa.interfaces.login.model.UserResponse;
import com.psi.ai_qa.interfaces.login.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping(Endpoints.LOGIN)
    public UserResponse login(@RequestBody UserRequest userRequest) {
        log.info("Login API hit with email: {}", userRequest.getEmail());
        return loginService.login(userRequest);
    }
}
