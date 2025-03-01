package faang.school.projectservice.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/fail")
    public String fail() {
        // Намеренная ошибка в логике
        return "This will fail!";
    }
}

