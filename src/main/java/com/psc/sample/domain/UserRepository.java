package com.psc.sample.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserInfo,Long> {
    Optional<UserInfo> findByEmail(String email);
}
