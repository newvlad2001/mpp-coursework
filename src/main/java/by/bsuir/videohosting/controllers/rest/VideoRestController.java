package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.annotations.IsUser;
import by.bsuir.videohosting.dto.request.EditVideoRequest;
import by.bsuir.videohosting.dto.response.VideoResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.Mark;
import by.bsuir.videohosting.models.Video;
import by.bsuir.videohosting.repository.MarkRepository;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.repository.VideoRepository;
import by.bsuir.videohosting.security.jwt.JwtUser;
import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EndPoints.API_VIDEO)
public class VideoRestController {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final MarkRepository markRepository;

    public VideoRestController(VideoRepository videoRepository, UserRepository userRepository, MarkRepository markRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.markRepository = markRepository;
    }

    /**
     * Get video by id
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getVideo(@PathVariable String id) {
        Video video = videoRepository.findById(UUID.fromString(id)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Video not found");
        }
        video.setViews(video.getViews() + 1);
        videoRepository.save(video);


        VideoResponse videoDto = VideoResponse.fromVideo(video);
        return ResponseJson.success().withValue(videoDto);
    }

    /**
     * Get all videos
     */
    @GetMapping
    public Page<VideoResponse> getVideo(Pageable pageable, @RequestParam(required = false) String name) {
        Page<Video> videos;
        if (name != null) {
            videos = videoRepository.findByIsPrivateAndNameContainsIgnoreCase(false, name, pageable);
        } else {
            videos = videoRepository.findByIsPrivate(false, pageable);
        }
        return videos.map(VideoResponse::fromVideo);
    }

    /**
     * Get user`s videos
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getVideoByUser(@PathVariable Integer id, @AuthenticationPrincipal JwtUser jwtUser) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return ResponseJson.error().withErrorMessage("Such user does not exist");
        List<Video> videos =
                jwtUser != null && jwtUser.getId().equals(user.getId()) ?
                        videoRepository.findByUser(user) :
                        videoRepository.findByUserAndIsPrivate(user, false);
        return ResponseJson.success().withValue(videos.stream().map(video -> VideoResponse.fromVideo(
                video,
                (int)markRepository.countByVideoAndMark(video, Mark.LIKE),
                (int)markRepository.countByVideoAndMark(video, Mark.DISLIKE)
        )));
    }

    /**
     * Delete video
     */
    @IsUser
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable String id,
                                         @AuthenticationPrincipal JwtUser jwtUser) throws IOException {
        Video video = videoRepository.findById(UUID.fromString(id)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Video not found");
        }

        User user = userRepository.findById(jwtUser.getId()).orElse(null);

        if (video.getUser().getId().equals(user.getId()) || user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            new File(".files/" + video.getVideo()).delete();
            videoRepository.delete(video);
            return ResponseJson.success().build();
        }
        return ResponseJson.error().withErrorMessage("Can`t delete");
    }

    /**
     * Edit video
     */
    @IsUser
    @PutMapping("{id}")
    public ResponseEntity<?> editVideo(@PathVariable String id,
                                         @RequestBody EditVideoRequest request,
                                         @AuthenticationPrincipal JwtUser jwtUser) {
        Video video = videoRepository.findById(UUID.fromString(id)).orElse(null);
        if (video == null) {
            return ResponseJson.error().withErrorMessage("Video not found");
        }

        User user = userRepository.findById(jwtUser.getId()).orElse(null);

        if (video.getUser().getId().equals(user.getId()) || user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            video.setName(request.getName());
            video.setAbout(request.getAbout());
            video.setIsPrivate(request.getIsPrivate());
            videoRepository.save(video);
            return ResponseJson.success().build();
        }
        return ResponseJson.error().withErrorMessage("Can`t edit");
    }
}
