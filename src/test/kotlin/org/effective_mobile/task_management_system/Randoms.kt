package org.effective_mobile.task_management_system

object Randoms {
    fun <E: Enum<E>> enum(enums: Class<E>): E {
        val enumConstants = enums.enumConstants
        return enumConstants[(enumConstants.indices).random()]
    }
}