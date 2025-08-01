package com.kpruncoach.api.trainingplan.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.kpruncoach.api.trainingplan.dto.CreateTrainingPlanRequest;
import com.kpruncoach.api.trainingplan.dto.TrainingPlanDTO;
import com.kpruncoach.api.trainingplan.model.PlanStatus;
import com.kpruncoach.api.trainingplan.service.TrainingPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/training-plans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    @Autowired
    public TrainingPlanController(TrainingPlanService trainingPlanService) {
        this.trainingPlanService = trainingPlanService;
    }

    @PostMapping
    @JsonView(TrainingPlanDTO.Views.Detailed.class)
    public ResponseEntity<TrainingPlanDTO> createTrainingPlan(
            @Valid @RequestBody CreateTrainingPlanRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        TrainingPlanDTO createdPlan = trainingPlanService.createTrainingPlan(request, userId);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @JsonView(TrainingPlanDTO.Views.Detailed.class)
    public ResponseEntity<TrainingPlanDTO> getTrainingPlan(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Optional<TrainingPlanDTO> plan = trainingPlanService.getTrainingPlanById(id, userId);
        return plan.map(p -> ResponseEntity.ok(p))
                  .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @JsonView(TrainingPlanDTO.Views.Detailed.class)
    public ResponseEntity<TrainingPlanDTO> updateTrainingPlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateTrainingPlanRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TrainingPlanDTO updatedPlan = trainingPlanService.updateTrainingPlan(id, request, userId);
            return ResponseEntity.ok(updatedPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingPlan(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            trainingPlanService.deleteTrainingPlan(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @JsonView(TrainingPlanDTO.Views.Summary.class)
    public ResponseEntity<List<TrainingPlanDTO>> getUserTrainingPlans(
            @RequestParam(required = false) PlanStatus status,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<TrainingPlanDTO> plans;

        if (status != null) {
            plans = trainingPlanService.getTrainingPlansByUserAndStatus(userId, status);
        } else {
            plans = trainingPlanService.getTrainingPlansByUser(userId);
        }

        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    @JsonView(TrainingPlanDTO.Views.Summary.class)
    public ResponseEntity<List<TrainingPlanDTO>> getActiveTrainingPlans(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<TrainingPlanDTO> activePlans = trainingPlanService.getActiveTrainingPlans(userId);
        return ResponseEntity.ok(activePlans);
    }

    @PostMapping("/{id}/publish")
    @JsonView(TrainingPlanDTO.Views.Detailed.class)
    public ResponseEntity<TrainingPlanDTO> publishTrainingPlan(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TrainingPlanDTO publishedPlan = trainingPlanService.publishTrainingPlan(id, userId);
            return ResponseEntity.ok(publishedPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/archive")
    @JsonView(TrainingPlanDTO.Views.Detailed.class)
    public ResponseEntity<TrainingPlanDTO> archiveTrainingPlan(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TrainingPlanDTO archivedPlan = trainingPlanService.archiveTrainingPlan(id, userId);
            return ResponseEntity.ok(archivedPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Assuming the username is the user ID or we have a way to get the user ID
        // This might need to be adjusted based on your actual UserDetails implementation
        return Long.parseLong(userDetails.getUsername());
    }
}
