package by.bsuir.videohosting.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import by.bsuir.videohosting.models.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String img;

    @JsonProperty
    private boolean isAdmin;

    @JsonIgnore
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .img(user.getImg())
                .name(user.getName())
                .isAdmin(user.getRoles().stream().anyMatch((role -> role.getName().equals("ROLE_ADMIN"))))
                .build();
    }

}
