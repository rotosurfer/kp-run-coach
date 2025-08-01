package com.kpruncoach.api.trainingplan.controller;

import com.kpruncoach.api.trainingplan.dto.CreateWorkoutRequest;
import com.kpruncoach.api.trainingplan.dto.WorkoutDTO;
import com.kpruncoach.api.trainingplan.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO> createWorkout(
            @Valid @RequestBody CreateWorkoutRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WorkoutDTO createdWorkout = workoutService.createWorkout(request, userId);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO> getWorkout(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Optional<WorkoutDTO> workout = workoutService.getWorkoutById(id, userId);
        return workout.map(w -> ResponseEntity.ok(w))
                     .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody CreateWorkoutRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            WorkoutDTO updatedWorkout = workoutService.updateWorkout(id, request, userId);
            return ResponseEntity.ok(updatedWorkout);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            workoutService.deleteWorkout(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/training-plan/{trainingPlanId}")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsByTrainingPlan(
            @PathVariable Long trainingPlanId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            List<WorkoutDTO> workouts = workoutService.getWorkoutsByTrainingPlan(trainingPlanId, userId);
            return ResponseEntity.ok(workouts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<WorkoutDTO> workouts = workoutService.getWorkoutsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<WorkoutDTO> workouts = workoutService.getWorkoutsByDate(userId, date);
        return ResponseEntity.ok(workouts);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkoutDTO> markWorkoutAsCompleted(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            WorkoutDTO completedWorkout = workoutService.markWorkoutAsCompleted(id, userId);
            return ResponseEntity.ok(completedWorkout);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/incomplete")
    public ResponseEntity<WorkoutDTO> markWorkoutAsIncomplete(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            WorkoutDTO incompleteWorkout = workoutService.markWorkoutAsIncomplete(id, userId);
            return ResponseEntity.ok(incompleteWorkout);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // This might need to be adjusted based on your actual UserDetails implementation
        return Long.parseLong(userDetails.getUsername());
    }
}
