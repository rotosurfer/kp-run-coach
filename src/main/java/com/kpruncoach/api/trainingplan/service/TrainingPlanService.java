package com.kpruncoach.api.trainingplan.service;

import com.kpruncoach.api.trainingplan.dto.CreateTrainingPlanRequest;
import com.kpruncoach.api.trainingplan.dto.TrainingPlanDTO;
import com.kpruncoach.api.trainingplan.model.PlanStatus;
import com.kpruncoach.api.trainingplan.model.TrainingPlan;
import com.kpruncoach.api.trainingplan.model.TrainingPlanTemplate;
import com.kpruncoach.api.trainingplan.repository.TrainingPlanRepository;
import com.kpruncoach.api.trainingplan.repository.TrainingPlanTemplateRepository;
import com.kpruncoach.api.user.model.User;
import com.kpruncoach.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainingPlanTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final TrainingPlanMapper trainingPlanMapper;

    @Autowired
    public TrainingPlanService(TrainingPlanRepository trainingPlanRepository,
                             TrainingPlanTemplateRepository templateRepository,
                             UserRepository userRepository,
                             TrainingPlanMapper trainingPlanMapper) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.trainingPlanMapper = trainingPlanMapper;
    }

    public TrainingPlanDTO createTrainingPlan(CreateTrainingPlanRequest request, Long userId) {
        validateTrainingPlanRequest(request, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        TrainingPlan trainingPlan = new TrainingPlan(
            request.getName(),
            request.getDescription(),
            request.getStartDate(),
            request.getEndDate(),
            user
        );

        // Set template if provided
        if (request.getTemplateId() != null) {
            TrainingPlanTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));
            trainingPlan.setTemplate(template);
        }

        TrainingPlan savedPlan = trainingPlanRepository.save(trainingPlan);
        return trainingPlanMapper.toDTO(savedPlan);
    }

    @Transactional(readOnly = true)
    public Optional<TrainingPlanDTO> getTrainingPlanById(Long planId, Long userId) {
        return trainingPlanRepository.findByIdAndUserId(planId, userId)
            .map(trainingPlanMapper::toDTO);
    }

    public TrainingPlanDTO updateTrainingPlan(Long planId, CreateTrainingPlanRequest request, Long userId) {
        TrainingPlan existingPlan = trainingPlanRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        validateTrainingPlanRequest(request, userId);

        existingPlan.setName(request.getName());
        existingPlan.setDescription(request.getDescription());
        existingPlan.setStartDate(request.getStartDate());
        existingPlan.setEndDate(request.getEndDate());

        // Update template if provided
        if (request.getTemplateId() != null) {
            TrainingPlanTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));
            existingPlan.setTemplate(template);
        }

        TrainingPlan updatedPlan = trainingPlanRepository.save(existingPlan);
        return trainingPlanMapper.toDTO(updatedPlan);
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanDTO> getTrainingPlansByUser(Long userId) {
        List<TrainingPlan> plans = trainingPlanRepository.findByUserId(userId);
        return plans.stream()
            .map(trainingPlanMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanDTO> getTrainingPlansByUserAndStatus(Long userId, PlanStatus status) {
        List<TrainingPlan> plans = trainingPlanRepository.findByUserIdAndStatus(userId, status);
        return plans.stream()
            .map(trainingPlanMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanDTO> getActiveTrainingPlans(Long userId) {
        List<TrainingPlan> plans = trainingPlanRepository.findActiveTrainingPlansByUserId(userId, LocalDate.now());
        return plans.stream()
            .map(trainingPlanMapper::toDTO)
            .collect(Collectors.toList());
    }

    public TrainingPlanDTO publishTrainingPlan(Long planId, Long userId) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (plan.getStatus() != PlanStatus.DRAFT) {
            throw new RuntimeException("Only draft plans can be published");
        }

        validatePlanForPublishing(plan);
        plan.setStatus(PlanStatus.PUBLISHED);

        TrainingPlan updatedPlan = trainingPlanRepository.save(plan);
        return trainingPlanMapper.toDTO(updatedPlan);
    }

    public TrainingPlanDTO archiveTrainingPlan(Long planId, Long userId) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        plan.setStatus(PlanStatus.ARCHIVED);

        TrainingPlan updatedPlan = trainingPlanRepository.save(plan);
        return trainingPlanMapper.toDTO(updatedPlan);
    }

    public void deleteTrainingPlan(Long planId, Long userId) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (plan.getStatus() == PlanStatus.PUBLISHED) {
            throw new RuntimeException("Cannot delete published training plans. Archive them instead.");
        }

        trainingPlanRepository.delete(plan);
    }

    private void validateTrainingPlanRequest(CreateTrainingPlanRequest request, Long userId) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Start date must be before end date");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }

        // Check for duplicate plan names for the user
        if (trainingPlanRepository.existsByUserIdAndNameAndStatus(userId, request.getName(), PlanStatus.PUBLISHED)) {
            throw new RuntimeException("A published training plan with this name already exists");
        }
    }

    private void validatePlanForPublishing(TrainingPlan plan) {
        if (plan.getWorkouts().isEmpty()) {
            throw new RuntimeException("Cannot publish a training plan without workouts");
        }

        if (plan.getName() == null || plan.getName().trim().isEmpty()) {
            throw new RuntimeException("Training plan must have a name");
        }
    }
}
