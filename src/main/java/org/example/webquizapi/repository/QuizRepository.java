package org.example.webquizapi.repository;

import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreator(AppUser creator);
}
