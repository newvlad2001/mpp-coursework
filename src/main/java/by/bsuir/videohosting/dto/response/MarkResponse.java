package by.bsuir.videohosting.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkResponse {

    private String videoId;

    private int likes;

    private int dislikes;

    private int markOwner;

}
