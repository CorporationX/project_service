package faang.school.projectservice.bjs_77842;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> filterBooks(FilterDto filterDto) {
        List<Book> allBooks = bookRepository.findAll();

        return allBooks.stream()
                .filter(book -> filterDto.getTitle() == null ||
                        (book.getTitle() != null && book.getTitle().contains(filterDto.getTitle())))
                .filter(book -> filterDto.getAuthor() == null ||
                        (book.getAuthor() != null && book.getAuthor().contains(filterDto.getAuthor())))
                .filter(book -> filterDto.getDescription() == null ||
                        (book.getDescription() != null && book.getDescription().contains(filterDto.getDescription())))
                .toList();
    }
}