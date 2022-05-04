package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.annotations.IsUser;
import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.dto.request.CreateMarkRequest;
import by.bsuir.videohosting.dto.response.MarkResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.Mark;
import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.models.Video;
import by.bsuir.videohosting.repository.MarkRepository;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.repository.VideoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(EndPoints.API_MARK)
public class MarkRestController {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final MarkRepository markRepository;

    public MarkRestController(UserRepository userRepository, VideoRepository videoRepository, MarkRepository markRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.markRepository = markRepository;
    }

    @IsUser
    @PostMapping
    public ResponseEntity createOrUpdateMark(@RequestBody CreateMarkRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Such user don`t exist");
        }
        Video video = videoRepository.findById(UUID.fromString(request.getVideoId())).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Such video don`t exist");
        }
        Mark mark = markRepository.findByUserAndVideo(user, video);
        if (mark != null) {
            mark.setMark(request.getMark());
        } else {
            mark = Mark.builder()
                    .mark(request.getMark())
                    .user(user)
                    .video(video)
                    .build();
        }
        mark = markRepository.save(mark);
        return ResponseJson.success().withValue(MarkResponse.builder()
                .dislikes((int)markRepository.countByVideoAndMark(video, Mark.DISLIKE))
                .likes((int)markRepository.countByVideoAndMark(video, Mark.LIKE))
                .markOwner(mark.getMark())
                .videoId(request.getVideoId())
                .build());
    }

    @GetMapping("{videoId}/user/{userId}")
    public ResponseEntity getMark(@PathVariable String videoId, @PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Such user does not exist");
        }
        Video video = videoRepository.findById(UUID.fromString(videoId)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Such video don`t exist");
        }
        Mark mark = markRepository.findByUserAndVideo(user, video);
        return ResponseJson.success().withValue(MarkResponse.builder()
                .dislikes((int)markRepository.countByVideoAndMark(video, Mark.DISLIKE))
                .likes((int)markRepository.countByVideoAndMark(video, Mark.LIKE))
                .markOwner(mark == null ? 0 : mark.getMark())
                .videoId(videoId)
                .build());
    }

    @GetMapping("{videoId}")
    public ResponseEntity getMark(@PathVariable String videoId) {
        Video video = videoRepository.findById(UUID.fromString(videoId)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Such video don`t exist");
        }
        return ResponseJson.success().withValue(MarkResponse.builder()
                .dislikes((int)markRepository.countByVideoAndMark(video, Mark.DISLIKE))
                .likes((int)markRepository.countByVideoAndMark(video, Mark.LIKE))
                .markOwner(0)
                .videoId(videoId)
                .build());
    }
}
