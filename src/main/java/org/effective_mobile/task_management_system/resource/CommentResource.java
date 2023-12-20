package org.effective_mobile.task_management_system.resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.pojo.task.CommentCreationRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Api.COMMENT)
@AllArgsConstructor
public class CommentResource {

    private CommentService commentService;

    @Tag(name = "Создание комментария")
    @PostMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody Long createComment(
        @PathVariable(Api.COMMENT_TASK_ID) Long taskId,
        @RequestBody @Valid CommentCreationRequestPojo commentCreationRequestPojo,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getUserId();
        Comment comment = commentService.createComment(userId, taskId, commentCreationRequestPojo);
        return comment.getId();
    }
}
