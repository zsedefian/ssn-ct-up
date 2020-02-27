package repositories;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import models.RedactedDocument;

import java.util.HashMap;
import java.util.Map;

public class DynamoRepository {

    private final AmazonDynamoDB dynamoDB;
    private final String tableName;

    public DynamoRepository() {
        this(AmazonDynamoDBAsyncClientBuilder.defaultClient(), System.getenv("TABLE_NAME"));
    }

    public DynamoRepository(AmazonDynamoDB dynamoDB, String tableName) {
        this.dynamoDB = dynamoDB;
        this.tableName = tableName;
    }

    public void save(RedactedDocument redactedDocument) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("objectKey", new AttributeValue(redactedDocument.getObjectKey()));
        item.put("date", new AttributeValue().withN(Long.toString(System.currentTimeMillis())));
        item.put("text", new AttributeValue(redactedDocument.getText()));
        int ssnCount = redactedDocument.getRedactedSsnList().size();
        item.put("ssnCount", new AttributeValue().withN(String.valueOf(ssnCount)));
        item.put("uploaderId", new AttributeValue(redactedDocument.getUploaderId()));
        item.put("phone-number", new AttributeValue("(555) 555-5555"));

        System.out.println("Saving document with " + ssnCount + " redacted SSNs to DynamoDB...");
        dynamoDB.putItem(tableName, item);
        System.out.println("Successfully saved image metadata to DynamoDB.");
    }
}
