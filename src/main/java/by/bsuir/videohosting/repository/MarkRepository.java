package by.bsuir.videohosting.repository;

import by.bsuir.videohosting.models.Mark;
import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, Integer> {
    Mark findByUserAndVideo(User user, Video video);
    long countByVideoAndMark(Video video, Integer mark);
}
