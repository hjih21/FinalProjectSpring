package practical.llm.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long userId;
    private String userEmail;
    private String userPw;
    private String userName;
    private LocalDate userBirth;
    private LocalDateTime createdAt;
}
