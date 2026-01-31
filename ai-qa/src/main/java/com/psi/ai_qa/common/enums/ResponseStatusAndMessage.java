package com.psi.ai_qa.common.enums;

public enum ResponseStatusAndMessage {

    INSUFFICIENT_BALANCE("400", "Insufficient balance"),
    WALLET_NOT_FOUND("404", "Wallet not found"),
    USER_NOT_FOUND("404", "User not found"),
    PASSWORD_DOES_NOT_MATCH("400","Password does not match"),
    TRANSFER_BLOCKED("403", "Transfer not allowed"),
    INVALID_AMOUNT("400", "Amount must be greater than zero");

    private final String statusCode;
    private final String message;

    // ✔ Constructor must match enum name
    ResponseStatusAndMessage(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
