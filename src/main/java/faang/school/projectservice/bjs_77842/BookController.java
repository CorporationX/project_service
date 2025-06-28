package faang.school.projectservice.bjs_77842;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
public class BookController {

    private final List<Book> books;

    @PostMapping("/books/filter")
    public ResponseEntity<List<Book>> filterBooks(@RequestBody @Valid FilterDto filterDto) {

        List<Book> result = books.stream()
                .filter(book -> filterDto.getTitle() == null || book.getTitle().contains(filterDto.getTitle()))
                .filter(book -> filterDto.getAuthor() == null || book.getAuthor().contains(filterDto.getAuthor()))
                .filter(book -> filterDto.getDescription() == null || book.getDescription().contains(filterDto.getDescription()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}