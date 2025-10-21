package practical.llm.user.dto;

import lombok.Data;

/**
 * 회원 프로필(이름, 생일) 수정 요청 DTO
 **/
@Data
public class UpdateProfileRequest {

    private String name;
    private String birth;
}
