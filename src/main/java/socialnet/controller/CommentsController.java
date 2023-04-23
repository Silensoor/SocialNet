package socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.CommonRs;
import socialnet.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @GetMapping("/api/v1/post/{postId}/comments")
    public ResponseEntity<CommonRs<List<CommentRs>>> getComments(@RequestHeader(name = "authorization") String jwtToken,
                                                                 @PathVariable Long postId,
                                                                 @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                                 @RequestParam(required = false, defaultValue = "20") Integer perPage
                              ) {
        CommonRs<List<CommentRs>> commonRs = commentService.getComments(postId, offset, perPage, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @PostMapping("/api/v1/post/{postId}/comments")
    public ResponseEntity<CommonRs<CommentRs>> createComment(@RequestHeader(name = "authorization") String jwtToken,
                                                             @PathVariable Long postId,
                                                             @RequestBody CommentRq commentRq) {
        CommonRs<CommentRs> commonRs = commentService.createComment(commentRq, postId, jwtToken);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity<CommonRs<CommentRs>> editComment(@RequestHeader(name = "authorization") String jwtToken,
                                                           @PathVariable Long id,
                                                           @PathVariable(name = "comment_id") Long commentId,
                                                           @RequestBody CommentRq commentRq) {
        CommonRs<CommentRs> commonRs = commentService.editComment(jwtToken, id, commentId, commentRq);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @DeleteMapping("/api/v1/post/{id}/comments/{comment_id}")
    public ResponseEntity<CommonRs<CommentRs>> deleteComment(@RequestHeader(name = "authorization") String jwtToken,
                                                             @PathVariable Long id,
                                                             @PathVariable(name = "comment_id") Long commentId
                                                             ) {
        CommonRs<CommentRs> commonRs = commentService.deleteComment(jwtToken, id, commentId);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);
    }
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}/recover")
    public ResponseEntity<CommonRs<CommentRs>> recoverComment(@RequestHeader(name = "authorization") String jwtToken,
                                                              @PathVariable Long id,
                                                              @PathVariable(name = "comment_id") Long commentId) {
        CommonRs<CommentRs> commonRs = commentService.recoverComment(jwtToken, id, commentId);
        return new ResponseEntity<>(commonRs, HttpStatus.OK);

    }
}
