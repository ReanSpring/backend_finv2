package com.example.finv2.repo;

import com.example.finv2.model.Newthing;
import com.example.finv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewthingRepo extends JpaRepository<Newthing, Long> {
//    find by user

//    findAllByUser
    List<Newthing> findAllByUser(User user);
}
