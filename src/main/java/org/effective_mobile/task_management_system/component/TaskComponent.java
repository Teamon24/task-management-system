package org.effective_mobile.task_management_system.component;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.converter.TaskConverter;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.enums.converter.PriorityConverter;
import org.effective_mobile.task_management_system.exception.AssignmentException;
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException;
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.pojo.TasksPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;
import org.effective_mobile.task_management_system.repository.FilteredAndPagedTaskRepository;
import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.effective_mobile.task_management_system.utils.MiscUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.effective_mobile.task_management_system.confing.CacheConfigurations.TASKS_CACHE;
import static org.effective_mobile.task_management_system.enums.Status.ASSIGNED;
import static org.effective_mobile.task_management_system.enums.Status.NEW;
import static org.effective_mobile.task_management_system.enums.UserRole.CREATOR;
import static org.effective_mobile.task_management_system.enums.UserRole.EXECUTOR;
import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

@Component
@AllArgsConstructor
public class TaskComponent {

    private final TaskRepository taskRepository;
    private final FilteredAndPagedTaskRepository filteredAndPagedTaskRepository;
    private final UserRepository userRepository;
    private final RoleComponent roleComponent;
    private final UserComponent userComponent;

    /**
     * Метод исключает циклическую зависимость. */
    @PostConstruct
    public void init() {
        userComponent.setTaskComponent(this);
    }

    @Cacheable(value = TASKS_CACHE, key = "#taskId")
    public Task getTask(Long taskId) {
        return taskRepository.findOrThrow(Task.class, taskId);
    }

    public Task createTask(User user, TaskCreationPayload taskCreationPayload) {
        Task newTask = TaskConverter.convert(taskCreationPayload, user);
        Task save = taskRepository.save(newTask);
        addRoleIfAbsent(user, CREATOR);
        return save;
    }

    @CachePut(cacheNames = TASKS_CACHE, key = "#taskId")
    public Task changeStatus(Long taskId, Status newStatus) {
        Task task = getTask(taskId);
        Status oldStatus = task.getStatus();
        if (Objects.equals(oldStatus, newStatus)) {
            String message = ExceptionMessages.getMessage(
                "exception.access.task.status.same", newStatus
            );
            throw new IllegalStatusChangeException(message);
        }
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public TaskJsonPojo getJsonPojo(Long id) {
        Task task = taskRepository.findOrThrow(Task.class, id);
        return TaskConverter.convert(task, true);
    }

    @CachePut(cacheNames = TASKS_CACHE, key = "#taskId")
    public Task setExecutor(Long taskId, User user) {
        Task task = taskRepository.findOrThrow(Task.class, taskId);
        User oldExecutor = task.getExecutor();
        throwIfSameExecutor(user, oldExecutor);
        addRoleIfAbsent(user, EXECUTOR);
        return setExecutorAndSave(task, user, Status.ASSIGNED);
    }

    @CachePut(cacheNames = TASKS_CACHE, key = "#taskId")
    public Task removeExecutor(Long taskId) {
        Task task = taskRepository.findOrThrow(Task.class, taskId);
        return setExecutorAndSave(task, null, Status.PENDING);
    }

    @CachePut(cacheNames = TASKS_CACHE, key = "#id")
    public Task editTask(Long id, TaskEditionPayload payload) {
        Task task = taskRepository.findOrThrow(Task.class, id);
        String newPriority = payload.getPriority();
        String newContent = payload.getContent();

        boolean toSave = false;
        if (StringUtils.isNotBlank(newContent)) {
            toSave = setIfNew(task, newContent, Task::getContent, Task::setContent);
        }

        if (newPriority != null) {
            Priority priority = new PriorityConverter().convert(newPriority);
            toSave = toSave | setIfNew(task, priority, Task::getPriority, Task::setPriority);
        }

        if (toSave) {
            return taskRepository.save(task);
        }

        String message = ExceptionMessages.getMessage(
            "exception.entity.task.update.nothing",
            Task.class.getSimpleName(),
            task.getId()
        );

        throw new NothingToUpdateInTaskException(message);
    }

    @CacheEvict(cacheNames = TASKS_CACHE, key="#id")
    public void deleteTask(Long id) {
        Task task = taskRepository.findOrThrow(Task.class, id);
        taskRepository.delete(task);
    }

    private Task setExecutorAndSave(Task task, User user, Status assigned) {
        task.setExecutor(user);
        task.setStatus(assigned);
        return taskRepository.save(task);
    }

    private void addRoleIfAbsent(User user, UserRole executor) {
        boolean userIsNotExecutor = user.getRoles()
            .stream()
            .noneMatch(role -> role.getName().equals(executor));

        if (userIsNotExecutor) {
            Role executorRole = roleComponent.findByName(executor);
            user.getRoles().add(executorRole);
            userRepository.save(user);
        }
    }

    private void throwIfSameExecutor(User user, User oldExecutor) {
        String oldUsername = MiscUtils.nullOrApply(oldExecutor, User::getUsername);
        if (Objects.equals(oldUsername, user.getUsername())) {
            String message = String.format("%s is already assigned.", oldExecutor.getUsername());
            throw new AssignmentException(message);
        }
    }

    private <V> boolean setIfNew(
        Task task,
        V newValue,
        Function<Task, V> getter,
        BiConsumer<Task, V> setter
    ) {
        if (newValue != null) {
            boolean valueIsNew = !Objects.equals(newValue, getter.apply(task));
            if (valueIsNew) {
                setter.accept(task, newValue);
                return true;
            }
        }
        return false;
    }

    public Status getStatus(Long taskId) {
        return getTask(taskId).getStatus();
    }

    public Page<Task> findByCreatorAndExecutor(TasksPayload tasksPayload, Pageable pageable) {
        return filteredAndPagedTaskRepository
            .findByCreatorAndExecutor(
                tasksPayload.getCreatorUsername(),
                tasksPayload.getExecutorUsername(),
                pageable
            );
    }

    public void validateStatusChange(Long taskId, Status newStatus) {
        switch (newStatus) {
            case ASSIGNED -> {
                String message = getMessage("exception.access.task.status.assign", ASSIGNED);
                throw new IllegalStatusChangeException(message);
            }
            case EXECUTING, DONE, PENDING -> userComponent.checkCurrentUserIsCreator(taskId);
            case NEW -> {
                String message = getMessage("exception.access.task.status.initial", NEW);
                throw new IllegalStatusChangeException(message);
            }
        };
    }
}