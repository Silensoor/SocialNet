package socialnet.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.CommonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "comments-controller", description = "Create, delete, read, edit and recover comments")
public class CommentsController {

    private final CommentService commentService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{postId}/comments")
    @ApiOperation(value = "get comment by id")
    public ResponseEntity<CommonRs<List<CommentRs>>> getComments(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter Long postId,
            @RequestParam(required = false, defaultValue = "0") @Parameter Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter Integer perPage) {
        CommonRs<List<CommentRs>> commonRs = commentService.getComments(postId, offset, perPage);
        return ResponseEntity.ok(commonRs);
    }


    @OnlineStatusUpdatable
    @PostMapping("/api/v1/post/{postId}/comments")
    @ApiOperation(value = "create comment")
    public ResponseEntity<CommonRs<CommentRs>> createComment(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter Long postId,
            @RequestBody @Parameter CommentRq commentRq) {
        CommonRs<CommentRs> commonRs = commentService.createComment(commentRq, postId, authorization);
        return ResponseEntity.ok(commonRs);
    }


    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}")
    @ApiOperation(value = "edit comment by id")
    public ResponseEntity<CommonRs<CommentRs>> editComment(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter Long id,
            @PathVariable(name = "comment_id") @Parameter Long commentId,
            @RequestBody @Parameter CommentRq commentRq) {
        CommonRs<CommentRs> commonRs = commentService.editComment(authorization, id, commentId, commentRq);
        return ResponseEntity.ok(commonRs);
    }


    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}/comments/{comment_id}")
    @ApiOperation(value = "delete comment by id")
    public ResponseEntity<CommonRs<CommentRs>> deleteComment(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter Long id,
            @PathVariable(name = "comment_id") @Parameter Long commentId) {
        CommonRs<CommentRs> commonRs = commentService.deleteComment(authorization, id, commentId);
        return ResponseEntity.ok(commonRs);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}/recover")
    @ApiOperation(value = "recover comment by id")
    public ResponseEntity<CommonRs<CommentRs>> recoverComment(
            @RequestHeader @Parameter String authorization,
            @PathVariable @Parameter Long id,
            @PathVariable(name = "comment_id") @Parameter Long commentId) {
        CommonRs<CommentRs> commonRs = commentService.recoverComment(authorization, id, commentId);
        return ResponseEntity.ok(commonRs);
    }
}
