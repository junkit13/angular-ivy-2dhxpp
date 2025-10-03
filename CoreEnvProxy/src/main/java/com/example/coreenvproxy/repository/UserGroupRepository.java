package com.example.coreenvproxy.repository;

import com.example.coreenvproxy.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
}
