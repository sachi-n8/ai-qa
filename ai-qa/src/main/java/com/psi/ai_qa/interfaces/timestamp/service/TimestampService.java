package com.psi.ai_qa.interfaces.timestamp.service;

import com.psi.ai_qa.interfaces.timestamp.model.TimestampRequest;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampResponse;

public interface TimestampService {
    TimestampResponse extract(TimestampRequest request);
}