package by.bsuir.videohosting.dto.request;

import lombok.Data;

@Data
public class CreateMarkRequest {
    private String videoId;
    private int userId;
    private int mark;
}
