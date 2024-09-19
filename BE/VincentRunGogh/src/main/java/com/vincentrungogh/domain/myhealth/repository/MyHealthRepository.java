package com.vincentrungogh.domain.myhealth.repository;

import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyHealthRepository extends JpaRepository<MyHealth, Integer> {
    Optional<MyHealth> findByUser(User user);
}
