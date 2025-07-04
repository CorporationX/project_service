package faang.school.projectservice.bjs_77842;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    @PostMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(@RequestBody @Valid FilterDto filterDto) {
        List<Book> result = bookService.filterBooks(filterDto);
        return ResponseEntity.ok(result);
    }
}