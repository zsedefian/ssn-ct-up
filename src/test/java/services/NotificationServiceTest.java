package services;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
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
public class NotificationServiceTest {

    @Mock
    private AmazonSNS snsClient;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void sendNotification_GivenDocWithRedactedSsn_SendMessage() {
        // given
        String topicArn = "this-is-an-arn";
        CreateTopicResult createTopicResult = new CreateTopicResult().withTopicArn(topicArn);
        RedactedDocument redactedDocument = new RedactedDocument(
                new BufferedImage(1, 1, 1),
                "this is text",
                List.of("3434"),
                "image/png"
        ).withUserCredentials(new UserCredentials("zach", "+15555555555"));

        // when
        when(snsClient.createTopic("ssn-ct-admin-topic")).thenReturn(createTopicResult);
        notificationService.sendNotification(redactedDocument);

        // then
        verify(snsClient, times(1)).createTopic("ssn-ct-admin-topic");
        verify(snsClient, times(1)).subscribe(any());
        verify(snsClient, times(1)).publish(any());
    }
}