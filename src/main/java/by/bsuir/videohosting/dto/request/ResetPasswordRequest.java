package by.bsuir.videohosting.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private int id;
    private String token;
    private String newPassword;
}
