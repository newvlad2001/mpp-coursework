package by.bsuir.videohosting.dto.request;

import by.bsuir.videohosting.models.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;


    public User toUser() {
        return User.builder()
                .email(email)
                .name(username)
                .passwordHash(password)
                .build();
    }
}
