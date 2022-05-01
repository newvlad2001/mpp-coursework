package by.bsuir.videohosting.repository;

import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.models.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    Video findByVideo(String video);
    List<Video> findByName(String name);
    List<Video> findByUser(User user);
    List<Video> findByUserAndIsPrivate(User user, boolean isPrivate);
    Page<Video> findByIsPrivate(boolean isPrivate, Pageable pageable);
    Page<Video> findByIsPrivateAndNameContainsIgnoreCase(boolean isPrivate, String name, Pageable pageable);
}
