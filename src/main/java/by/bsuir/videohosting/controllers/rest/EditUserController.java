package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.annotations.IsAdmin;
import by.bsuir.videohosting.annotations.IsUser;
import by.bsuir.videohosting.dto.request.EditUserAdminRequest;
import by.bsuir.videohosting.dto.request.EditUserRequest;
import by.bsuir.videohosting.dto.response.UserOwnerResponse;
import by.bsuir.videohosting.dto.response.UserResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.repository.RoleRepository;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.repository.VideoRepository;
import by.bsuir.videohosting.security.jwt.JwtTokenProvider;
import by.bsuir.videohosting.security.jwt.JwtUser;
import by.bsuir.videohosting.service.UserService;
import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@RestController
@RequestMapping(EndPoints.API_USER)
public class EditUserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public EditUserController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RoleRepository roleRepository, VideoRepository videoRepository, UserService userService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    @IsUser
    @PutMapping
    public ResponseEntity editUser(@AuthenticationPrincipal JwtUser jwtUser,
                                   @RequestBody EditUserRequest request,
                                   HttpServletRequest servletRequest) {

        User user = userRepository.findById(jwtUser.getId()).orElse(null);
        user.setName(request.getUsername());
        user.setImg(request.getImg());
        User userFromDb = userRepository.findByName(request.getUsername()).orElse(null);
        if (userFromDb != null && !userFromDb.getId().equals(user.getId())) {
            return ResponseJson.error().withErrorMessage("Такой пользователь уже существует");
        }
        user = userRepository.save(user);

        userService.logout(user, jwtTokenProvider.resolveToken(servletRequest));
        String token = jwtTokenProvider.createToken(user.getName(), user.getRoles());
        userService.saveToken(user, token);
        UserOwnerResponse response = UserOwnerResponse.fromUser(user, token);
        return ResponseJson.success().withValue(response);
    }

    @IsAdmin
    @PutMapping("/admin")
    public ResponseEntity editUserAdmin(@AuthenticationPrincipal JwtUser jwtUser,
                                        @RequestBody EditUserAdminRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Такого пользователя не существует");
        }
        if (request.getIsAdmin()) {
            if (user.getRoles().stream().noneMatch((role -> role.getName().equals("ROLE_ADMIN")))) {
                user.getRoles().add(roleRepository.findByName("ROLE_ADMIN"));
                userRepository.save(user);
            }
        } else {
            user.getRoles().removeIf(role -> role.getName().equals("ROLE_ADMIN"));
            userRepository.save(user);
        }
        return ResponseJson.success().build();
    }

    @IsUser
    @GetMapping("{userId}")
    public ResponseEntity getUser(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Такого пользователя не существует");
        }
        return ResponseJson.success().withValue(UserResponse.fromUser(user));
    }

    @IsAdmin
    @DeleteMapping("{userId}")
    public ResponseEntity deleteUser(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseJson.error().withErrorMessage("Такого пользователя не существует");
        }
        videoRepository.findByUser(user).forEach(video -> {
            new File(".files/" + video.getVideo()).delete();
            videoRepository.delete(video);
        });

        userRepository.delete(user);
        return ResponseJson.success().withValue(UserResponse.fromUser(user));
    }
}
