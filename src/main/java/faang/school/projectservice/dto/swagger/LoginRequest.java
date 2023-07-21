package faang.school.projectservice.dto.swagger;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request details")
public class LoginRequest {

    @Schema(description = "Login Id")
    private String loginId;

    @Schema(description = "password")
    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}