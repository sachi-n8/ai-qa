package com.psi.ai_qa.interfaces.document.service;

import com.psi.ai_qa.interfaces.document.model.DocumentDashboardResponse;
import com.psi.ai_qa.interfaces.document.model.DocumentResponse;

import java.util.List;

public interface DocumentService {
    DocumentDashboardResponse listDocuments(String userId);
}
