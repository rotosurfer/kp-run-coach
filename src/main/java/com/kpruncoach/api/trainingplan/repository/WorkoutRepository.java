package com.kpruncoach.api.trainingplan.repository;

import com.kpruncoach.api.trainingplan.model.Workout;
import com.kpruncoach.api.trainingplan.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByTrainingPlanId(Long trainingPlanId);

    List<Workout> findByTrainingPlanIdOrderByScheduledDateAsc(Long trainingPlanId);

    List<Workout> findByTrainingPlanIdAndScheduledDateBetween(Long trainingPlanId, LocalDate startDate, LocalDate endDate);

    List<Workout> findByTrainingPlanUserIdAndScheduledDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT w FROM Workout w WHERE w.trainingPlan.user.id = :userId AND w.scheduledDate = :date ORDER BY w.createdAt ASC")
    List<Workout> findByUserIdAndScheduledDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    List<Workout> findByTrainingPlanIdAndType(Long trainingPlanId, WorkoutType type);

    List<Workout> findByTrainingPlanIdAndCompleted(Long trainingPlanId, Boolean completed);

    @Query("SELECT COUNT(w) FROM Workout w WHERE w.trainingPlan.id = :trainingPlanId AND w.completed = true")
    long countCompletedWorkoutsByTrainingPlanId(@Param("trainingPlanId") Long trainingPlanId);
}
