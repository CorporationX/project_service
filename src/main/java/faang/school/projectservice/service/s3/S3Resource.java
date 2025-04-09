package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.S3Object;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.AbstractResource;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class S3Resource extends AbstractResource implements AutoCloseable {

    @NotNull(message = "S3Object cant be null")
    private final S3Object s3Object;
    /**
     * -- GETTER --
     *  Возвращает имя файла, ассоциированное с ресурсом.
     *
     */
    @Getter
    private final String fileName;

    /**
     * Возвращает описание ресурса для логгирования и диагностики.
     *
     * @return Строка описания ресурса в формате "S3 resource [bucket/key]"
     */
    @Override
    public String getDescription() {
        return "S3 resource [" + s3Object.getBucketName() + "/" + s3Object.getKey() + "]";
    }

    /**
     * Открывает поток для чтения данных из S3-объекта.
     *
     * @return InputStream для чтения содержимого файла
     * @throws IllegalStateException если S3-объект уже был закрыт
     */
    @Override
    public InputStream getInputStream() {
        return s3Object.getObjectContent();
    }

    /**
     * Возвращает размер содержимого S3-объекта в байтах.
     *
     * @return Размер файла в байтах
     */
    @Override
    public long contentLength() {
        return s3Object.getObjectMetadata().getContentLength();
    }

    /**
     * Закрывает ресурс и освобождает все связанные с ним системные ресурсы.
     * <p>
     * Последовательность закрытия:
     * <ol>
     *   <li>Закрывается поток данных (InputStream)</li>
     *   <li>Закрывается соединение с S3 (S3Object)</li>
     * </ol>
     * </p>
     *
     */
    @Override
    public void close() throws IOException {
        try {
            getInputStream().close();
        } finally {
            if (s3Object != null) {
                s3Object.close();
            }
        }
    }
}
