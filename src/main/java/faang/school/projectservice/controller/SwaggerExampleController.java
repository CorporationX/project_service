package faang.school.projectservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * swagger url: http://localhost:8082/swagger-ui/index.html#/
 * swagger documentation: http://localhost:8082/v3/api-docs
 */
@RestController
@RequestMapping("/swagger")
@Tag(name = "Swagger example controller", description = "description of the controller")
public class SwaggerExampleController {

    @GetMapping()
    @Operation(
            summary = "Brief description of the method",
            description = "more detailed description"
    )
    @ApiResponse(responseCode = "400", description = "param must be filled")
    public HttpStatus get(@RequestParam int param) {
        return HttpStatus.OK;
    }
}
