package com.devsense.repository;

import com.devsense.model.entity.Review;
import com.devsense.model.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByUserEmail(String email, Pageable pageable);

    Optional<Review> findByIdAndUserEmail(UUID id, String email);

    // Used by ReviewConsumer to update status atomically
    @Modifying
    @Query("UPDATE Review r SET r.status = :status, r.startedAt = :startedAt WHERE r.id = :id")
    void updateStatusAndStartTime(@Param("id") UUID id,
                                  @Param("status") ReviewStatus status,
                                  @Param("startedAt") LocalDateTime startedAt);

    @Modifying
    @Query("UPDATE Review r SET r.status = :status, r.overallScore = :score, " +
            "r.summary = :summary, r.completedAt = :completedAt WHERE r.id = :id")
    void updateCompleted(@Param("id") UUID id, @Param("status") ReviewStatus status,
                         @Param("score") BigDecimal score,
                         @Param("summary") String summary,
                         @Param("completedAt") LocalDateTime completedAt);

    @Modifying
    @Query("UPDATE Review r SET r.status = :status, r.errorMessage = :msg WHERE r.id = :id")
    void updateFailed(@Param("id") UUID id, @Param("status") ReviewStatus status,
                      @Param("msg") String msg);
}
