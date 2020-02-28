package services;

import models.RedactedDocument;
import models.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import repositories.DynamoRepository;
import repositories.S3Repository;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceServiceTest {

    @Mock
    private S3Repository s3Repository;
    @Mock
    private DynamoRepository dynamoRepository;
    @InjectMocks
    private PersistenceService persistenceService;

    @Test
    public void save_GivenValidRedactedDocument_SaveSuccessfully() {
        // given
        RedactedDocument redactedDocument = new RedactedDocument(
                new BufferedImage(1, 1, 1),
                "this is text",
                List.of("list", "of", "items"),
                "image/png"
        ).withUserCredentials(new UserCredentials("zach", "555-555-5555"));

        // when
        persistenceService.save(redactedDocument);

        // then
        verify(s3Repository, atMostOnce()).save(redactedDocument);
        verify(dynamoRepository, atMostOnce()).save(redactedDocument);
        verifyNoMoreInteractions(s3Repository, dynamoRepository);
    }
}