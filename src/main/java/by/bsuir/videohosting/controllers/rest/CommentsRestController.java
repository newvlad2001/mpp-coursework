package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.annotations.IsAdmin;
import by.bsuir.videohosting.annotations.IsUser;
import by.bsuir.videohosting.dto.request.CreateCommentRequest;
import by.bsuir.videohosting.dto.response.CommentResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.Comment;
import by.bsuir.videohosting.models.Video;
import by.bsuir.videohosting.repository.CommentRepository;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.repository.VideoRepository;
import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EndPoints.API_COMMENTS)
public class CommentsRestController {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public CommentsRestController(CommentRepository commentRepository, VideoRepository videoRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/video/{id}")
    public ResponseEntity getCommentsByVideo(@PathVariable String id) {
        Video video = videoRepository.findById(UUID.fromString(id)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Видео не найдено");
        }

        List<Comment> comments = commentRepository.findByVideo(video);

        return ResponseJson.success().withValue(comments.stream().map(CommentResponse::fromComment));
    }

    @IsUser
    @PostMapping
    public ResponseEntity createCommentFroVideo(@RequestBody CreateCommentRequest request) {
        User user = userRepository.findById(request.getUser_id()).orElse(null);
        if (user == null)
            return ResponseJson.error().withErrorMessage("Пользователь не найден");
        Video video = videoRepository.findById(UUID.fromString(request.getVideo_id())).orElse(null);
        if (video == null)
            return ResponseJson.error().withErrorMessage("Видео не найдено");

        Comment comment = Comment.builder()
                .text(request.getText())
                .user(user)
                .video(video)
                .build();
        return ResponseJson.success().withValue(CommentResponse.fromComment(commentRepository.save(comment)));
    }


    @IsAdmin
    @DeleteMapping("{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Integer id) {
        commentRepository.deleteById(id);
        return ResponseJson.success().build();
    }
}
