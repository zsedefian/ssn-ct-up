package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import models.RedactedDocument;

import java.util.*;

public class NotificationService {

    private final AmazonSNS snsClient;
    private final Set<String> adminPhoneNumbers;

    public NotificationService() {
        this(
                AmazonSNSClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1)  // The default region, US_EAST_2, does not support SNS topics.
                        .build(),
                System.getenv("ADMIN_PHONE_NUMBERS") // Comma-separated list
        );
    }

    public NotificationService(AmazonSNS snsClient, String adminPhoneNumbersEnvVar) {
        this.snsClient = snsClient;
        adminPhoneNumbers = new HashSet<>();
        adminPhoneNumbers.addAll(List.of(adminPhoneNumbersEnvVar.split(",")));
    }

    /**
     * Sends an SMS to the user who uploaded an image that contains one or more
     * social security numbers, as well as any registered phone numbers of administrators.
     */
    public void sendNotifications(RedactedDocument redactedDocument) {
        String message = "SS # detected: " + String.join(", ", redactedDocument.getRedactedSsnList());
        String uploaderPhoneNumber = redactedDocument.getUserCredentials().getPhoneNumber();
        sendSmsToAdmins(message);
        sendSmsToUploader(uploaderPhoneNumber, message);
    }

    private void sendSmsToAdmins(String message) {
        String topicArn = snsClient.createTopic("ssn-ct-admin-topic").getTopicArn();
        adminPhoneNumbers.forEach(adminPhoneNumber ->
                snsClient.subscribe(new SubscribeRequest(topicArn, "sms", adminPhoneNumber)));

        System.out.println("Sending SMS message to administrators...");
        PublishResult result = snsClient.publish(new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(message)
                .withMessageAttributes(createSmsAttributes()));
        System.out.println("Successfully sent SMS message to administrators: " + result);
    }

    private void sendSmsToUploader(String uploaderPhoneNumber, String message) {
        System.out.println("Sending SMS to uploading user...");
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(uploaderPhoneNumber)
                .withMessageAttributes(createSmsAttributes()));
        System.out.println("Successfully sent SMS to uploading user: " + result); // Prints the message ID.
    }

    private Map<String, MessageAttributeValue> createSmsAttributes() {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") // The sender ID shown on the device.
                .withDataType("String"));
        return smsAttributes;
    }
}
