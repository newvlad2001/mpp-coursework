package by.bsuir.videohosting.controllers.rest;

import by.bsuir.videohosting.annotations.IsUser;
import by.bsuir.videohosting.consts.EndPoints;
import by.bsuir.videohosting.dto.request.AuthenticationRequest;
import by.bsuir.videohosting.dto.response.UserOwnerResponse;
import by.bsuir.videohosting.dto.response.http.ResponseJson;
import by.bsuir.videohosting.models.User;
import by.bsuir.videohosting.repository.UserRepository;
import by.bsuir.videohosting.security.jwt.JwtTokenProvider;
import by.bsuir.videohosting.security.jwt.JwtUser;
import by.bsuir.videohosting.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(EndPoints.API_AUTH)
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PutMapping("/check")
    public ResponseEntity loginToken(final Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "not auth"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }



    @PostMapping
    public ResponseEntity login(@RequestBody AuthenticationRequest requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByName(username);
            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }


            String token = jwtTokenProvider.createToken(username, user.getRoles());

            userService.saveToken(user, token);

            UserOwnerResponse response = UserOwnerResponse.fromUser(user, token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect login or password");
        }
    }

    @IsUser
    @DeleteMapping
    public ResponseEntity logout(
            @AuthenticationPrincipal JwtUser jwtUser,
            HttpServletRequest request) {
        User user = userRepository.findById(jwtUser.getId()).orElse(null);

        if (user == null) {
            return ResponseJson.error().withErrorMessage("User not found");
        }

        String token = jwtTokenProvider.resolveToken(request);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseJson.error().withErrorMessage("Incorrect token");
        }
        userService.logout(user, token);

        return ResponseJson.success().build();
    }
}
