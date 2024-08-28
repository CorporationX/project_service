package faang.school.projectservice.controller.ping;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    @Operation(summary = "Test endpoint for testing spring server")
    public String PingGet() {
        return "Pong!";
    }
}
