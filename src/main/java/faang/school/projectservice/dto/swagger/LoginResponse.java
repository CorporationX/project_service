package faang.school.projectservice.dto.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response details")
public class LoginResponse {

    @Schema(description = "Response string")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}