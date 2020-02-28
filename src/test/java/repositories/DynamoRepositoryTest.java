package repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import models.RedactedDocument;
import models.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DynamoRepositoryTest {

    @Mock
    private AmazonDynamoDB dynamoDB;

    private DynamoRepository dynamoRepository;

    @Before
    public void setup() {
        dynamoRepository = new DynamoRepository(dynamoDB, "TABLE_NAME");
    }

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
        dynamoRepository.save(redactedDocument);

        // then
        verify(dynamoDB, times(1)).putItem(any(), any());
    }
}