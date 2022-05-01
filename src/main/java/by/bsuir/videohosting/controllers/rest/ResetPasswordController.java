package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.dto.request.ResetGeneratePasswordRequest;
import by.bsuir.videohosting.dto.request.ResetPasswordRequest;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.security.jwt.JwtTokenProvider;
import by.bsuir.videohosting.service.MailService;
import by.bsuir.videohosting.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(EndPoints.API_RESET_PASSWORD)
public class ResetPasswordController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    public ResetPasswordController(UserService userService, UserRepository userRepository, MailService mailService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping
    public ResponseEntity createToken(@RequestBody ResetGeneratePasswordRequest request) {
        try {
            User user = userService.generateResetToken(request.getEmail());
            mailService.send(request.getEmail(),
                    "Восстановление пароля",
                    "Ссылка для восстановление пароля: http://localhost:8080/reset?token=" + user.getToken() + "&id=" + user.getId());

            return ResponseJson.success().build();
        } catch (IOException e) {
            return ResponseJson.error().withErrorMessage("Пользователя с такой почтой не существует");
        }
    }

    @PutMapping
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByIdAndToken(request.getId(), request.getToken()).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Неверный токен восстановления пароля");
        }
        userService.setNewPassword(user, request.getNewPassword());

        return ResponseJson.success().build();
    }
}
