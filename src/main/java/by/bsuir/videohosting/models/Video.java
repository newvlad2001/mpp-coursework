package by.bsuir.videohosting.models;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Videos")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue
    @Type(type="uuid-char")
    private UUID id;

    private String name;

    private String video;

    private String about;

    @Column(name = "is_private")
    private Boolean isPrivate;

    private Long views;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
