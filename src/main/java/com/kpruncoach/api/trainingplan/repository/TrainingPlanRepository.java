package com.kpruncoach.api.trainingplan.repository;

import com.kpruncoach.api.trainingplan.model.PlanStatus;
import com.kpruncoach.api.trainingplan.model.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    List<TrainingPlan> findByUserIdAndStatus(Long userId, PlanStatus status);

    List<TrainingPlan> findByUserId(Long userId);

    Optional<TrainingPlan> findByIdAndUserId(Long id, Long userId);

    List<TrainingPlan> findByUserIdAndStartDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.user.id = :userId AND tp.status = :status ORDER BY tp.createdAt DESC")
    List<TrainingPlan> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("status") PlanStatus status);

    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.user.id = :userId AND tp.endDate >= :currentDate ORDER BY tp.startDate ASC")
    List<TrainingPlan> findActiveTrainingPlansByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);

    boolean existsByUserIdAndNameAndStatus(Long userId, String name, PlanStatus status);
}
