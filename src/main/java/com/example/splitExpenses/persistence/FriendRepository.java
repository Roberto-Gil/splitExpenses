package com.example.splitExpenses.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    @Query("select case when count(f)> 0 then true else false end from Friend f where f.name = :name")
    boolean existsFriendByName(String name);

    @Query("select f.id from Friend f where f.name = :name")
    Optional<Integer> getIdFromName(String name);

}
