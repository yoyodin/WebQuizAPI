package org.example.webquizapi.repository;

import org.example.webquizapi.dto.CompletionDto;
import org.example.webquizapi.entity.Completion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompletionRepository extends JpaRepository<Completion, Long> {
    @Query(value = """
            SELECT new org.example.webquizapi.dto.CompletionDto(q.id, c.completedAt)
            FROM Completion c
            JOIN c.quiz q
            WHERE c.appUser.email = :email
            """)
    Page<CompletionDto> findByEmail(@Param("email") String email, Pageable pageable);
}
