package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.Drawing;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawingRepository extends JpaRepository<Drawing, Integer> {

    List<Drawing> findAllByUser(User user);

    List<Drawing> findAllByUserAndIsCompleted(User user, Boolean isCompleted);

    List<Drawing> findAllByUserAndIsCreatedBoard(User user, Boolean isCreatedBoard);

    int countAllByUserAndIsCompleted(User user, Boolean isCompleted);
}
