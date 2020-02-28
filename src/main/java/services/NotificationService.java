package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import models.RedactedDocument;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {

    // This is hard-coded here because the rest of the AWS resources are in us-east-2, where topics cannot be created.
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:116621101481:ssn-ct-admin-topic";

    private final AmazonSNS snsClient;

    public NotificationService() {
        // As above, need to set SNS to EU_WEST_1 because US_EAST_2 does not support SNS topics.
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
        subscribeToTopic(redactedDocument);
        String message = "SS # detected: " + String.join(", ", redactedDocument.getRedactedSsnList());
        Map<String, MessageAttributeValue> smsAttributes = createSmsAttributes();
        sendMessageToTopic(message, smsAttributes);
    }

    private void subscribeToTopic(RedactedDocument redactedDocument) {
        String uploaderPhoneNumber = redactedDocument.getUserCredentials().getPhoneNumber();
        SubscribeRequest subscribeRequest = new SubscribeRequest(TOPIC_ARN, "sms", uploaderPhoneNumber);
        snsClient.subscribe(subscribeRequest);
    }

    private Map<String, MessageAttributeValue> createSmsAttributes() {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") // The sender ID shown on the device.
                .withDataType("String"));
        return smsAttributes;
    }

    private void sendMessageToTopic(String message, Map<String, MessageAttributeValue> smsAttributes) {
        System.out.println("Sending SMS message...");
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(TOPIC_ARN)
                .withMessage(message)
                .withMessageAttributes(smsAttributes));
        System.out.println("Successfully sent SMS message: " + result);
    }
}
