package com.vincentrungogh.domain.board.repository;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.board.entity.UserLike;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, Integer> {
    
    List<UserLike> findAllByUser(User user);

    // user-board like 관계 찾기
    Optional<UserLike> findByUserAndBoard(User user, Board board);
}
