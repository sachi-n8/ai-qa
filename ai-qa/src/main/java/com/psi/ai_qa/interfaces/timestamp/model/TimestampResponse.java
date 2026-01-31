package com.psi.ai_qa.interfaces.timestamp.model;

import lombok.Data;

@Data
public class TimestampResponse {
    private String topic;
    private double start;
    private double end;
}