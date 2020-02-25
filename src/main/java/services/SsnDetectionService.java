package services;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.*;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SsnDetectionService {

    private static final Pattern SSN_PATTERN = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");

    private AmazonTextract textract;

    public SsnDetectionService() {
        this(AmazonTextractClientBuilder.defaultClient());
    }

    public SsnDetectionService(AmazonTextract textract) {
        this.textract = textract;
    }

    public List<Block> detectSsnBlocks(Document document) {
        DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest()
                .withDocument(document);
        DetectDocumentTextResult detectDocumentTextResult = textract
                .detectDocumentText(detectDocumentTextRequest);
        return detectDocumentTextResult.getBlocks().stream()
                .filter(block -> block.getBlockType().equals(BlockType.WORD.toString())
                        && SSN_PATTERN.matcher(block.getText()).matches())
                .collect(Collectors.toList());
    }
}
