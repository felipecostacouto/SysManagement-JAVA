package com.felipecostacouto.javamanagesys.dao;

import com.felipecostacouto.javamanagesys.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDAO extends JpaRepository<User,Integer> {

    User findByEmailId(@Param("email") String email);

}
