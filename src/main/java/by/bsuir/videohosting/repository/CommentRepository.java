package by.bsuir.videohosting.repository;

import by.bsuir.videohosting.models.Comment;
import by.bsuir.videohosting.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByVideo(Video video);
}
