package com.admin.user.admin.user.repo;

import com.admin.user.admin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<User,Long> {

    @Query(value = "select * from auth_task where username = :username", nativeQuery = true)
    User loginApi(String username);

    User findByUsername(String username);
}
