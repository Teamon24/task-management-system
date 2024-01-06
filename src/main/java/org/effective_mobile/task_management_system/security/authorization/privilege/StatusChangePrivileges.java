package org.effective_mobile.task_management_system.security.authorization.privilege;

import java.util.Set;

public interface StatusChangePrivileges {
    Set<String> canStartNewTask();

    Set<String> canSuspendTask();

    Set<String> canChangeStatusOfActiveTask();
}
