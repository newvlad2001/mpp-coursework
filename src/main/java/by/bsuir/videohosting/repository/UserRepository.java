package by.bsuir.videohosting.repository;

import by.bsuir.videohosting.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndToken(Integer id, String token);
    boolean existsByName(String name);
}
