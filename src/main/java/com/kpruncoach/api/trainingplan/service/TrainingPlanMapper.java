package com.kpruncoach.api.trainingplan.service;

import com.kpruncoach.api.trainingplan.dto.TrainingPlanDTO;
import com.kpruncoach.api.trainingplan.dto.WorkoutDTO;
import com.kpruncoach.api.trainingplan.model.TrainingPlan;
import com.kpruncoach.api.trainingplan.model.Workout;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingPlanMapper {

    public TrainingPlanDTO toDTO(TrainingPlan trainingPlan) {
        if (trainingPlan == null) {
            return null;
        }

        TrainingPlanDTO dto = new TrainingPlanDTO();
        dto.setId(trainingPlan.getId());
        dto.setName(trainingPlan.getName());
        dto.setDescription(trainingPlan.getDescription());
        dto.setStartDate(trainingPlan.getStartDate());
        dto.setEndDate(trainingPlan.getEndDate());
        dto.setStatus(trainingPlan.getStatus());
        dto.setUserId(trainingPlan.getUser().getId());
        dto.setUserName(trainingPlan.getUser().getFirstName() + " " + trainingPlan.getUser().getLastName());
        dto.setVersion(trainingPlan.getVersion());
        dto.setCreatedAt(trainingPlan.getCreatedAt());
        dto.setUpdatedAt(trainingPlan.getUpdatedAt());

        if (trainingPlan.getTemplate() != null) {
            dto.setTemplateId(trainingPlan.getTemplate().getId());
            dto.setTemplateName(trainingPlan.getTemplate().getName());
        }

        if (trainingPlan.getWorkouts() != null && !trainingPlan.getWorkouts().isEmpty()) {
            List<WorkoutDTO> workoutDTOs = trainingPlan.getWorkouts().stream()
                .map(this::toWorkoutDTO)
                .collect(Collectors.toList());
            dto.setWorkouts(workoutDTOs);
        }

        return dto;
    }

    public WorkoutDTO toWorkoutDTO(Workout workout) {
        if (workout == null) {
            return null;
        }

        WorkoutDTO dto = new WorkoutDTO();
        dto.setId(workout.getId());
        dto.setName(workout.getName());
        dto.setDescription(workout.getDescription());
        dto.setScheduledDate(workout.getScheduledDate());
        dto.setType(workout.getType());
        dto.setDistanceKm(workout.getDistanceKm());
        dto.setDurationMinutes(workout.getDurationMinutes());
        dto.setTargetPace(workout.getTargetPace());
        dto.setNotes(workout.getNotes());
        dto.setCompleted(workout.getCompleted());
        dto.setTrainingPlanId(workout.getTrainingPlan().getId());
        dto.setCreatedAt(workout.getCreatedAt());
        dto.setUpdatedAt(workout.getUpdatedAt());

        return dto;
    }
}
