package com.psi.ai_qa.interfaces.login.service;


import com.psi.ai_qa.interfaces.login.model.UserRequest;
import com.psi.ai_qa.interfaces.login.model.UserResponse;

public interface LoginService {

    public UserResponse login(UserRequest userRequest);

}
