package org.effective_mobile.task_management_system.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends AbstractEntity {

    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Builder
    public Comment(String content, User user, Task task, Date creationDate) {
        this.content = content;
        this.user = user;
        this.task = Objects.requireNonNull(task);
        this.creationDate = Objects.requireNonNull(creationDate);
    }

    @PostLoad
    public void postConstruct() {
        // TODO: сделать так, чтобы комментарий удаленного пользователя корректно обрабатывался, т.е.
        // если пользователь удален, то отдавать вместо имя пользователя
        // информацию об отсутствии пользователя в системе
        this.user = Objects.requireNonNullElse(
            this.getUser(),
            User.builder().username("Пользователь удален").build()
        );
    }
}