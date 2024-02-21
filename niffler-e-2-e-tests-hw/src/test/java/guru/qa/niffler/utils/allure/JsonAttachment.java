package guru.qa.niffler.utils.allure;

import io.qameta.allure.attachment.AttachmentData;

public class JsonAttachment implements AttachmentData {

    private final String name;
    private final String json;

    public JsonAttachment(String name, String json) {
        this.name = name;
        this.json = json;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getJson() {
        return json;
    }
}
