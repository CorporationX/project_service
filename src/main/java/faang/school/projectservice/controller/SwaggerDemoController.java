package faang.school.projectservice.controller;

import faang.school.projectservice.dto.swagger.LoginRequest;
import faang.school.projectservice.dto.swagger.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SwaggerDemoController {
    @Operation(summary = "isRunning", description = "To check whether service is running or not")
    @GetMapping(value = "/isRunning", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Service is running.", HttpStatus.OK);
    }

    @Operation(summary = "/user/login", description = "To login user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "1001", description = "Application specific error.") })
    @PostMapping(value = "/user/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest requestDTO) {
        LoginResponse responseDTO = new LoginResponse();
        responseDTO.setResponse("Success");
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
