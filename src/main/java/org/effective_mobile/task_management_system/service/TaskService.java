package org.effective_mobile.task_management_system.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.ContextComponent;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.component.UserComponent;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.ChangedStatusResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.utils.converter.TaskConverter;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.effective_mobile.task_management_system.utils.MiscUtils.evalIfNotNull;
import static org.effective_mobile.task_management_system.utils.MiscUtils.nullOrApply;

@Service
@AllArgsConstructor
public class TaskService {

    private ContextComponent contextComponent;
    private UserComponent userComponent;
    private TaskComponent taskComponent;

    @Transactional
    public TaskResponsePojo createTask(@NotNull Long userId, TaskCreationRequestPojo taskCreationRequestPojo) {
        User creator = userComponent.getById(userId);
        Task task = taskComponent.createTask(creator, taskCreationRequestPojo);
        return TaskConverter.convert(task, false);
    }

    public TaskResponsePojo getTask(@NotNull Long id) {
        Task task = taskComponent.getTask(id);
        return TaskConverter.convert(task, true);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public void deleteTask(@NotNull Long id) {
        taskComponent.deleteTask(id);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public AssignmentResponsePojo setExecutor(
        @NotNull Long taskId,
        @NotNull String newExecutorUsername
    ) {
        User newExecutor = userComponent.getByUsername(newExecutorUsername);
        Task task = taskComponent.getTask(taskId);
        User oldExecutor = task.getExecutor();
        taskComponent.setExecutor(task, newExecutor);
        String oldExecutorUsername = nullOrApply(oldExecutor, User::getUsername);
        return new AssignmentResponsePojo(taskId, newExecutor.getUsername(), oldExecutorUsername);
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#taskId)")
    public Long unassign(@NotNull Long taskId) {
        Task task = taskComponent.getTask(taskId);
        return taskComponent.removeExecutor(task).getId();
    }

    @Transactional
    @PreAuthorize("@authorizationComponent.currentUserIsCreator(#id)")
    public TaskResponsePojo editTask(@NotNull Long id, TaskEditionRequestPojo requestPojo) {
        Task task = taskComponent.getTask(id);
        Task editedTask = taskComponent.editTask(task, requestPojo);
        return TaskConverter.convert(editedTask, false);
    }

    @Transactional
    public ChangedStatusResponsePojo setStatus(Long taskId, Status newStatus) {
        Task task = taskComponent.getTask(taskId);
        CustomUserDetails principal = contextComponent.getPrincipal();
        userComponent.validateStatusChange(task, newStatus, principal);
        Status oldStatus = task.getStatus();
        taskComponent.changeStatus(task, newStatus);
        return new ChangedStatusResponsePojo(taskId, oldStatus, newStatus);
    }

    public Page<TaskResponsePojo> getByCreatorOrExecutor(
        TasksFiltersRequestPojo tasksFiltersRequestPojo,
        Pageable pageable
    ) {
        String creatorUsername = tasksFiltersRequestPojo.getCreatorUsername();
        String executorUsername = tasksFiltersRequestPojo.getExecutorUsername();

        evalIfNotNull(creatorUsername, (s) -> { userComponent.checkUsernameExists(s); });
        evalIfNotNull(executorUsername, (s) -> { userComponent.checkUsernameExists(s); });

        return taskComponent
            .findByCreatorAndExecutor(tasksFiltersRequestPojo, pageable)
            .map(task -> {
                Boolean withComments = tasksFiltersRequestPojo.getWithComments();
                return TaskConverter.convert(task, withComments);
            });
    }
}