package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.dto.request.RegisterRequest;
import by.bsuir.videohosting.dto.response.UserResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.service.UserService;
import by.bsuir.videohosting.consts.EndPoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndPoints.API_REG)
public class RegisterRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    public RegisterRestController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest) {

        User userFromDbName = userService.findByName(registerRequest.getUsername());
        User userFromDbEmail = userRepository.findByEmail(registerRequest.getEmail()).orElse(null);
        if (userFromDbName != null || userFromDbEmail != null) {
            return ResponseJson.error().withErrorMessage("Такой пользователь уже существует");
        }

        User regUser = userService.register(registerRequest.toUser());
        if (regUser == null) {
            return ResponseJson.error().withErrorMessage("Error register");
        }
        return ResponseJson.success().withValue(UserResponse.fromUser(regUser));
    }
}
