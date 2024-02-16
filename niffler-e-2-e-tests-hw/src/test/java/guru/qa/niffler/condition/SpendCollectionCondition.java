package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.spend.SpendJson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpendCollectionCondition {

    public static CollectionCondition spends(SpendJson... expectedSPends) {
        return new CollectionCondition() {

            final List<String> expectedTexts = new ArrayList<>();
            final List<String> actualTexts = new ArrayList<>();

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (elements.size() != expectedSPends.length) {
                    return CheckResult.rejected("Incorrect table size: " + elements.size(), elements);
                }

                for (WebElement element : elements) {

                    List<String> actualLineText = element.findElements(By.cssSelector("td")).stream()
                            .map(WebElement::getText)
                            .filter(text -> !"".equals(text))
                            .toList();

                    actualTexts.addAll(actualLineText);
                }

                for (SpendJson expectedSPend : expectedSPends) {
                    expectedTexts.add(new SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
                            .format(expectedSPend.spendDate()));
                    expectedTexts.add(String.valueOf(expectedSPend.amount()));
                    expectedTexts.add(String.valueOf(expectedSPend.currency()));
                    expectedTexts.add(expectedSPend.category());
                    expectedTexts.add(expectedSPend.description());
                }

                if (expectedTexts.equals(actualTexts)) {
                    return CheckResult.accepted();
                }

                return CheckResult.rejected("Incorrect spends content", actualTexts);
            }

            @Override
            public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
                List<String> actualTexts = lastCheckResult.getActualValue();

                if (actualTexts == null || actualTexts.isEmpty()) {
                    throw new ElementNotFound(collection, toString(), timeoutMs, cause);
                }

                String message = lastCheckResult.getMessageOrElse(() -> "Texts mismatch");

                throw new TextsMismatch(message, collection, expectedTexts, actualTexts, explanation, timeoutMs, cause);
            }

            @Override
            public boolean missingElementSatisfiesCondition() {
                return false;
            }
        };
    }
}
