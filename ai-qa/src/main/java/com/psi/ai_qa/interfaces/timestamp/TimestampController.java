package com.psi.ai_qa.interfaces.timestamp;

import com.psi.ai_qa.common.constants.Endpoints;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampRequest;
import com.psi.ai_qa.interfaces.timestamp.model.TimestampResponse;
import com.psi.ai_qa.interfaces.timestamp.service.TimestampService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.AI_QA)
@RequiredArgsConstructor
public class TimestampController {

    private final TimestampService timestampService;

    @PostMapping(Endpoints.TIME_STAMP)
    public TimestampResponse extract(@RequestBody TimestampRequest request) {
        return timestampService.extract(request);
    }
}