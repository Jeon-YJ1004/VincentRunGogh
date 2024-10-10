package com.vincentrungogh.domain.route.repository;

import com.vincentrungogh.domain.route.entity.Route;
import com.vincentrungogh.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    List<Route> findAllByUser(User user);

    //유저가 아닌 것들 모두 조회
    List<Route> findAllByUserIsNotOrderByCreatedDesc(User user);
}
