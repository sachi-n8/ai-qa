package com.psi.ai_qa.interfaces.login.model;

import lombok.Data;

@Data
public class UserResponse {

    private String userID;

    private String statusCode;

    private String statusMessage;

    private String jwtToken;

}
