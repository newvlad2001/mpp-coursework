package by.bsuir.videohosting.repository;

import by.bsuir.videohosting.models.Token;
import by.bsuir.videohosting.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findByUser(User user);
    Token findByToken(String token);
    void deleteByUser(User user);
    void deleteTokenByUserAndToken(User user, String token);
    void deleteTokenByToken(String token);
}
