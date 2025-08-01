package com.kpruncoach.api.trainingplan.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.kpruncoach.api.trainingplan.model.PlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TrainingPlanDTO {

    public static class Views {
        public static class Summary {}
        public static class Detailed extends Summary {}
        public static class Admin extends Detailed {}
    }

    @JsonView(Views.Summary.class)
    private Long id;

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detailed.class)
    private String description;

    @JsonView(Views.Summary.class)
    private LocalDate startDate;

    @JsonView(Views.Summary.class)
    private LocalDate endDate;

    @JsonView(Views.Summary.class)
    private PlanStatus status;

    @JsonView(Views.Summary.class)
    private Long userId;

    @JsonView(Views.Detailed.class)
    private String userName;

    @JsonView(Views.Detailed.class)
    private Long templateId;

    @JsonView(Views.Detailed.class)
    private String templateName;

    @JsonView(Views.Detailed.class)
    private List<WorkoutDTO> workouts;

    @JsonView(Views.Admin.class)
    private Long version;

    @JsonView(Views.Summary.class)
    private LocalDateTime createdAt;

    @JsonView(Views.Detailed.class)
    private LocalDateTime updatedAt;

    // Constructors
    public TrainingPlanDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<WorkoutDTO> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<WorkoutDTO> workouts) {
        this.workouts = workouts;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
