package org.effective_mobile.task_management_system.resource;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.enums.converter.StatusConverter;
import org.effective_mobile.task_management_system.pojo.PageResponse;
import org.effective_mobile.task_management_system.pojo.TasksFiltersPayload;
import org.effective_mobile.task_management_system.pojo.assignment.AssignmentResponse;
import org.effective_mobile.task_management_system.pojo.task.ChangedStatusResponse;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.service.TaskService;
import org.effective_mobile.task_management_system.validator.ValidEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.effective_mobile.task_management_system.docs.Docs.TASK_PATH_VAR_DESCRIPTION;

@RestController
@RequestMapping(Api.TASK)
@AllArgsConstructor
public class TaskResource {

    private final TaskService taskService;


    @Tag(name = "Создание")
    @PostMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskJsonPojo createTask(
        @RequestBody @Valid TaskCreationPayload taskCreationPayload,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return taskService.createTask(customUserDetails.getUserId(), taskCreationPayload);
    }

    @Tag(name = "Получение")
    @GetMapping("/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskJsonPojo getTask(
        @NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id
    ) {
        return taskService.getTask(id);
    }

    @Tag(name = "Пагинация и фильтрация")
    @GetMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody PageResponse<TaskJsonPojo> getTasks(
        @RequestBody @Valid  TasksFiltersPayload tasksFiltersPayload,
        @RequestParam(defaultValue = "0", name = "page") @NotNull int pageNumber,
        @RequestParam(defaultValue = "10", name = "size") @NotNull int size
    ) {
        Pageable page = PageRequest.of(pageNumber, size);
        Page<TaskJsonPojo> tasksPage = taskService.getByCreatorOrExecutor(tasksFiltersPayload, page);
        return new PageResponse<>(tasksPage);
    }

    @Tag(name = "Редактирование")
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = TaskJsonPojo.class)) })
    })
    @PutMapping("/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskJsonPojo editTask(
        @NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id,
        @RequestBody @Valid @NonNull TaskEditionPayload taskEditionPayload
    ) {
        return taskService.editTask(id, taskEditionPayload);
    }

    @Tag(name = "Удаление")
    @DeleteMapping("/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public void deleteTask(@NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id) {
        taskService.deleteTask(id);
    }

    @Tag(name = "Назначение исполнителя")
    @PutMapping("/{id}" + Api.EXECUTOR)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody AssignmentResponse setExecutor(
        @NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id,
        @RequestParam(Api.EXECUTOR_USERNAME) String executorUsername
    ) {
        return taskService.setExecutor(id, executorUsername);
    }

    @Tag(name = "Удаление исполнителя")
    @PutMapping("/{id}" + Api.UNASSIGN)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public Long removeExecutor(@NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id) {
        return taskService.unassign(id);
    }

    @Tag(name = "Изменение статуса")
    @PutMapping("/{id}" + Api.STATUS)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody ChangedStatusResponse setStatus(
        @NotNull @PathVariable @Parameter(description = TASK_PATH_VAR_DESCRIPTION)  Long id,
        @RequestParam(name = Api.NEW_STATUS_PARAM) @ValidEnum(clazz = Status.class) String newStatusStr
    ) {
        Status newStatus = new StatusConverter().convert(newStatusStr);
        return taskService.setStatus(id, newStatus);
    }

}
