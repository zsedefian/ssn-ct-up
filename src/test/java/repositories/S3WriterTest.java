package repositories;

import com.amazonaws.services.s3.AmazonS3;
import models.RedactedDocument;
import models.UserCredentials;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3WriterTest {

    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private S3Writer s3Writer;

    @Test
    public void save_GivenValidRedactedDocument_SaveSuccessfully() {
        // given
        RedactedDocument redactedDocument = new RedactedDocument(
                new BufferedImage(1, 1, 1),
                "this is text",
                List.of("list", "of", "items"),
                "image/png"
        ).withUserCredentials(new UserCredentials("zach", "+15555555555"));

        // when
        s3Writer.save(redactedDocument);

        // then
        verify(s3Client, times(1)).putObject(any());
        verifyNoMoreInteractions(s3Client);
    }
}