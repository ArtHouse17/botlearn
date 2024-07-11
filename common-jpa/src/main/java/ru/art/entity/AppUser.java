package ru.art.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import ru.art.entity.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;
    @CreationTimestamp
    private LocalDateTime firstLogInData;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState state;
}
