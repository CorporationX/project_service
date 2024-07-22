package faang.school.projectservice.controller;


import faang.school.projectservice.dto.client.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Test swagger")
public class Controller {

    @PostMapping
    @Operation(summary = "Добавить user")
    public void post(@RequestBody UserDto userDto) {
        //...
    }

    @GetMapping
    @Operation(summary = "Получить список User")
    public void getAll() {
        //...
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить User")
    public void put(@PathVariable long id) {
        //...
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить User")
    public void delete(@PathVariable long id) {
        //...
    }
}
