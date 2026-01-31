package com.psi.ai_qa.interfaces.auth;


import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.common.constants.StatusAndMessage;
import com.psi.ai_qa.common.model.User;
import com.psi.ai_qa.interfaces.auth.service.AuthSerivce;
import com.psi.ai_qa.interfaces.login.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
public class AuthController {

    @Autowired
    private AuthSerivce authSerivce;

    @PostMapping(Endpoints.AUTH)
    public UserResponse auth() {
        User user = new User();
        UserResponse response = new UserResponse();
        String token = authSerivce.authenticate(user);
        response.setJwtToken(token);
        response.setStatusCode(StatusAndMessage.SUCCESS);
        response.setStatusMessage(StatusAndMessage.JWT_TOKEN);
        return response;
    }
}