package com.psi.ai_qa.common.exception;


import com.psi.ai_qa.common.enums.ResponseStatusAndMessage;

public class WalletException extends RuntimeException {

    private final String statusCode;   // your custom status code

    public WalletException(ResponseStatusAndMessage response) {
        super(response.getMessage());
        this.statusCode = response.getStatusCode();
    }

    public String getStatusCode() {
        return statusCode;
    }
}
