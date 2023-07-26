package com.myblog.api.repository;

import com.myblog.api.domain.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, Long> {

}
