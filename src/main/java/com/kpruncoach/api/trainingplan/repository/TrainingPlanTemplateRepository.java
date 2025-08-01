package com.kpruncoach.api.trainingplan.repository;

import com.kpruncoach.api.trainingplan.model.TrainingPlanTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanTemplateRepository extends JpaRepository<TrainingPlanTemplate, Long> {

    List<TrainingPlanTemplate> findByIsPublicTrue();

    List<TrainingPlanTemplate> findByDifficultyLevel(String difficultyLevel);

    @Query("SELECT t FROM TrainingPlanTemplate t WHERE t.isPublic = true AND t.durationWeeks BETWEEN :minWeeks AND :maxWeeks")
    List<TrainingPlanTemplate> findPublicTemplatesByDurationRange(@Param("minWeeks") Integer minWeeks, @Param("maxWeeks") Integer maxWeeks);

    @Query("SELECT t FROM TrainingPlanTemplate t WHERE t.isPublic = true AND LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TrainingPlanTemplate> searchPublicTemplatesByName(@Param("searchTerm") String searchTerm);
}
