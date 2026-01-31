package com.psi.ai_qa.common.repo;


import com.psi.ai_qa.common.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {

    Optional<User> findByEmail(String email) ;

    Boolean existsUserByUserId(String userId);
}
