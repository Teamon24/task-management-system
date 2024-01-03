package org.effective_mofile.task_management_system

import net.datafaker.Faker
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.utils.enums.Priority
import org.effective_mobile.task_management_system.utils.enums.Status

object RandomTasks {
    fun task(user: User, executor: User? = null): Task =
        Task.builder()
            .content(content())
            .status(Randoms.enum(Status::class.java))
            .priority(Randoms.enum(Priority::class.java))
            .creator(user)
            .executor(executor)
            .build()

    private val text = Faker().text()
    fun content(): String = text.text(30)
    fun priority() = Randoms.enum(Priority::class.java).name
}

