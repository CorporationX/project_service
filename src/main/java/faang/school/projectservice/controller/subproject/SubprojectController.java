package faang.school.projectservice.controller.subproject;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subproject")
@Tag(name = "Подпроекты")
public class SubprojectController {
    @PostMapping("/create")
    public void createSubproject() {

    }
}
