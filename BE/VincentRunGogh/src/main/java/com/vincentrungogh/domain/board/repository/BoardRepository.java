package com.vincentrungogh.domain.board.repository;

import com.vincentrungogh.domain.board.entity.Board;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board,Integer> {

    List<Board> findAllByOrderByCreatedDesc();

    List<Board> findByRouteUserOrderByCreatedDesc(User user);

    Optional<Board> findById(int id);

}
