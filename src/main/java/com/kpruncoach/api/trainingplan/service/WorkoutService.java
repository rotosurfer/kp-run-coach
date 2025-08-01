package com.kpruncoach.api.trainingplan.service;

import com.kpruncoach.api.trainingplan.dto.CreateWorkoutRequest;
import com.kpruncoach.api.trainingplan.dto.WorkoutDTO;
import com.kpruncoach.api.trainingplan.model.TrainingPlan;
import com.kpruncoach.api.trainingplan.model.Workout;
import com.kpruncoach.api.trainingplan.repository.TrainingPlanRepository;
import com.kpruncoach.api.trainingplan.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainingPlanMapper trainingPlanMapper;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository,
                         TrainingPlanRepository trainingPlanRepository,
                         TrainingPlanMapper trainingPlanMapper) {
        this.workoutRepository = workoutRepository;
        this.trainingPlanRepository = trainingPlanRepository;
        this.trainingPlanMapper = trainingPlanMapper;
    }

    public WorkoutDTO createWorkout(CreateWorkoutRequest request, Long userId) {
        TrainingPlan trainingPlan = trainingPlanRepository.findByIdAndUserId(request.getTrainingPlanId(), userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        validateWorkoutRequest(request, trainingPlan);

        Workout workout = new Workout(
            request.getName(),
            request.getScheduledDate(),
            request.getType(),
            trainingPlan
        );

        workout.setDescription(request.getDescription());
        workout.setDistanceKm(request.getDistanceKm());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setTargetPace(request.getTargetPace());
        workout.setNotes(request.getNotes());

        Workout savedWorkout = workoutRepository.save(workout);
        return trainingPlanMapper.toWorkoutDTO(savedWorkout);
    }

    @Transactional(readOnly = true)
    public Optional<WorkoutDTO> getWorkoutById(Long workoutId, Long userId) {
        return workoutRepository.findById(workoutId)
            .filter(workout -> workout.getTrainingPlan().getUser().getId().equals(userId))
            .map(trainingPlanMapper::toWorkoutDTO);
    }

    public WorkoutDTO updateWorkout(Long workoutId, CreateWorkoutRequest request, Long userId) {
        Workout existingWorkout = workoutRepository.findById(workoutId)
            .filter(workout -> workout.getTrainingPlan().getUser().getId().equals(userId))
            .orElseThrow(() -> new RuntimeException("Workout not found"));

        validateWorkoutRequest(request, existingWorkout.getTrainingPlan());

        existingWorkout.setName(request.getName());
        existingWorkout.setDescription(request.getDescription());
        existingWorkout.setScheduledDate(request.getScheduledDate());
        existingWorkout.setType(request.getType());
        existingWorkout.setDistanceKm(request.getDistanceKm());
        existingWorkout.setDurationMinutes(request.getDurationMinutes());
        existingWorkout.setTargetPace(request.getTargetPace());
        existingWorkout.setNotes(request.getNotes());

        Workout updatedWorkout = workoutRepository.save(existingWorkout);
        return trainingPlanMapper.toWorkoutDTO(updatedWorkout);
    }

    public void deleteWorkout(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
            .filter(w -> w.getTrainingPlan().getUser().getId().equals(userId))
            .orElseThrow(() -> new RuntimeException("Workout not found"));

        workoutRepository.delete(workout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutDTO> getWorkoutsByTrainingPlan(Long trainingPlanId, Long userId) {
        // Verify the training plan belongs to the user
        trainingPlanRepository.findByIdAndUserId(trainingPlanId, userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        List<Workout> workouts = workoutRepository.findByTrainingPlanIdOrderByScheduledDateAsc(trainingPlanId);
        return workouts.stream()
            .map(trainingPlanMapper::toWorkoutDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkoutDTO> getWorkoutsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Workout> workouts = workoutRepository.findByTrainingPlanUserIdAndScheduledDateBetween(userId, startDate, endDate);
        return workouts.stream()
            .map(trainingPlanMapper::toWorkoutDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkoutDTO> getWorkoutsByDate(Long userId, LocalDate date) {
        List<Workout> workouts = workoutRepository.findByUserIdAndScheduledDate(userId, date);
        return workouts.stream()
            .map(trainingPlanMapper::toWorkoutDTO)
            .collect(Collectors.toList());
    }

    public WorkoutDTO markWorkoutAsCompleted(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
            .filter(w -> w.getTrainingPlan().getUser().getId().equals(userId))
            .orElseThrow(() -> new RuntimeException("Workout not found"));

        workout.setCompleted(true);
        Workout updatedWorkout = workoutRepository.save(workout);
        return trainingPlanMapper.toWorkoutDTO(updatedWorkout);
    }

    public WorkoutDTO markWorkoutAsIncomplete(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
            .filter(w -> w.getTrainingPlan().getUser().getId().equals(userId))
            .orElseThrow(() -> new RuntimeException("Workout not found"));

        workout.setCompleted(false);
        Workout updatedWorkout = workoutRepository.save(workout);
        return trainingPlanMapper.toWorkoutDTO(updatedWorkout);
    }

    private void validateWorkoutRequest(CreateWorkoutRequest request, TrainingPlan trainingPlan) {
        if (request.getScheduledDate().isBefore(trainingPlan.getStartDate()) ||
            request.getScheduledDate().isAfter(trainingPlan.getEndDate())) {
            throw new RuntimeException("Workout scheduled date must be within the training plan date range");
        }

        if (request.getDistanceKm() != null && request.getDistanceKm() <= 0) {
            throw new RuntimeException("Distance must be positive");
        }

        if (request.getDurationMinutes() != null && request.getDurationMinutes() <= 0) {
            throw new RuntimeException("Duration must be positive");
        }
    }
}
