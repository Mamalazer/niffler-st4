package guru.qa.niffler.utils.allure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import lombok.SneakyThrows;

public class JsonAppender {

    private final AttachmentProcessor<AttachmentData> attachmentProcessor = new DefaultAttachmentProcessor();

    @SneakyThrows
    public void attachJson(String name, Object json) {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        JsonAttachment jsonAttachment = new JsonAttachment(name, objectWriter.writeValueAsString(json));
        attachmentProcessor.addAttachment(jsonAttachment, new FreemarkerAttachmentRenderer("json-attach.ftl"));
    }
}
