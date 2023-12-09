package org.effective_mobile.task_management_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность задания, создаваемого пользователем.
 * Пользователи могут управлять своими задачами:
 * создавать новые,
 * редактировать существующие,
 * просматривать и удалять,
 * менять статус и
 * назначать исполнителей задачи.
 * Пользователи могут просматривать задачи других пользователей, а исполнители задачи могут менять статус своих задач.
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "tasks")
public class Task extends AbstractEntity{

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Priority priority;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Setter
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "task_id")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Task(
        Priority priority,
        Status status,
        String content,
        User creator,
        User executor,
        List<Comment> comments
    ) {
        this.priority = Objects.requireNonNullElse(priority, Priority.LOW);
        this.status = Objects.requireNonNullElse(status, Status.NEW);
        this.content = content;
        this.executor = executor;
        this.creator = Objects.requireNonNull(creator);
        this.comments = comments;
    }
}