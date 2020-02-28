package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import models.RedactedDocument;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {

    private final AmazonSNS snsClient;

    public NotificationService() {
        // The default region, US_EAST_2, does not support SNS topics.
        this(AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build());
    }

    public NotificationService(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    /**
     * Sends an SMS to the user who uploaded an image that contains one or more
     * social security numbers, as well as any registered phone numbers of administrators.
     */
    public void sendNotification(RedactedDocument redactedDocument) {
        String topicArn = snsClient.createTopic("ssn-ct-admin-topic").getTopicArn();
        subscribeToTopic(topicArn, redactedDocument);
        String message = "SS # detected: " + String.join(", ", redactedDocument.getRedactedSsnList());
        Map<String, MessageAttributeValue> smsAttributes = createSmsAttributes();
        sendMessageToTopic(topicArn, message, smsAttributes);
    }

    private void subscribeToTopic(String topicArn, RedactedDocument redactedDocument) {
        String uploaderPhoneNumber = redactedDocument.getUserCredentials().getPhoneNumber();
        SubscribeRequest subscribeRequest = new SubscribeRequest(topicArn, "sms", uploaderPhoneNumber);
        snsClient.subscribe(subscribeRequest);
    }

    private Map<String, MessageAttributeValue> createSmsAttributes() {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") // The sender ID shown on the device.
                .withDataType("String"));
        return smsAttributes;
    }

    private void sendMessageToTopic(String topicArn, String message, Map<String, MessageAttributeValue> smsAttributes) {
        System.out.println("Sending SMS message...");
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message)
                .withMessageAttributes(smsAttributes));
        System.out.println("Successfully sent SMS message: " + result);
    }
}
