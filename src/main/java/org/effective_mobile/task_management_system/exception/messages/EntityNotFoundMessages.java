package org.effective_mobile.task_management_system.exception.messages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.database.entity.AbstractEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityNotFoundMessages {
    public static <E extends AbstractEntity, ID> String notFound(Class<E> entityClass, ID id) {
        return ExceptionMessages.getMessage(
            "exception.entity.notFound.id",
            entityClass.getSimpleName(),
            id
        );
    }
}
