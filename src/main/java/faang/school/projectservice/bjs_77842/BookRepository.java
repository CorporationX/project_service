package faang.school.projectservice.bjs_77842;

import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleAndAuthorAndDescription(
            String title, String author, String description);

}