package services;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Geometry;
import models.RedactedDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SsnRedactionServiceTest {

    @Mock
    AmazonTextract textract;

    @InjectMocks
    private SsnRedactionService ssnRedactionService;

    @Test
    public void redact_GivenImageAsBase64EncodedString_RedactSsn() {
        // given
        String base64EncodedStringOfAnImageContainingOneSsn = getImageWithSsnAsBase64EncodedString();
        DetectDocumentTextResult detectDocumentTextResult = new DetectDocumentTextResult().withBlocks(
                List.of(
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("This"),
                        new Block().withBlockType("SOMETHING_ELSE").withGeometry(getGeometry()).withText("OMIT"),
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("is"),
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("an"),
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("example"),
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("555-55-3434"),
                        new Block().withBlockType("WORD").withGeometry(getGeometry()).withText("4143-4-1231")
                )
        );

        // when
        when(textract.detectDocumentText(any())).thenReturn(detectDocumentTextResult);

        RedactedDocument redactedDocument = ssnRedactionService.redact(base64EncodedStringOfAnImageContainingOneSsn);

        // then
        assertNotNull(redactedDocument.getImage());
        assertNotNull(redactedDocument.getObjectKey());
        assertEquals(redactedDocument.getRedactedSsnList().size(), 1);
        assertEquals(redactedDocument.getRedactedSsnList().get(0), "3434");
        assertEquals(redactedDocument.getText(), "This is an example *** 4143-4-1231");
        assertEquals(redactedDocument.getMimeType(), "image/png");
    }

    private Geometry getGeometry() {
        return new Geometry().withBoundingBox(
                new BoundingBox()
                        .withLeft(Float.valueOf("1"))
                        .withHeight(Float.valueOf("1"))
                        .withTop(Float.valueOf("1"))
                        .withWidth(Float.valueOf("1"))
        );
    }

    private String getImageWithSsnAsBase64EncodedString() {
        try {
            return Files.readString(
                    Paths.get(this.getClass().getResource("/base64_encoded_image.txt").toURI()),
                    StandardCharsets.UTF_8
            ).trim();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
