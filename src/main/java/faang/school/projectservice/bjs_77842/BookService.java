package faang.school.projectservice.bjs_77842;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private final BookController bookController;

    public List<Book> getAllBooks() {
        return bookController.findAll();
    }
}