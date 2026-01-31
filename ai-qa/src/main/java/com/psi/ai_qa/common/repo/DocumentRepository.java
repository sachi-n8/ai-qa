package com.psi.ai_qa.common.repo;

import com.psi.ai_qa.common.model.DocumentChunk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends MongoRepository<DocumentChunk,String> {

    Optional<DocumentChunk> findByUserId(String userId);

    List<DocumentChunk> findByUserIdOrderByUploadTimeDesc(String userId);

}
