package org.effective_mobile.task_management_system.utils.enums;

/**
 * Статус задачи. */
public enum Priority implements ValuableEnum<String> {
    LOW,
    AVERAGE,
    HIGH;

    @Override
    public String getValue() {
        return this.name();
    }
}
