package com.psi.ai_qa.interfaces.auth.service;

import com.psi.ai_qa.common.model.User;

public interface AuthSerivce {
    public String authenticate(User user);
}
