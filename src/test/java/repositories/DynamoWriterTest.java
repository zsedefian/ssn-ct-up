package repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import models.RedactedDocument;
import models.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DynamoWriterTest {

    @Mock
    private AmazonDynamoDB dynamoDB;

    private DynamoWriter dynamoWriter;

    @Before
    public void setup() {
        dynamoWriter = new DynamoWriter(dynamoDB, "TABLE_NAME");
    }

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
        dynamoWriter.save(redactedDocument);

        // then
        verify(dynamoDB, times(1)).putItem(any(), any());
    }
}