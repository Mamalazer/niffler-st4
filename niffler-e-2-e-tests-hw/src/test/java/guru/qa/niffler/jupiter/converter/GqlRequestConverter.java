package guru.qa.niffler.jupiter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.GqlRequestFile;
import guru.qa.niffler.model.gql.GqlRequest;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class GqlRequestConverter implements ArgumentConverter {

  private static final ObjectMapper om = new ObjectMapper();

  @Override
  public Object convert(Object o, ParameterContext parameterContext) throws ArgumentConversionException {
    if (parameterContext.isAnnotated(GqlRequestFile.class) && o instanceof String path) {
      try (InputStream is = new ClassPathResource(path).getInputStream()) {
        return om.readValue(is, GqlRequest.class);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    throw new RuntimeException("can`t convert to GqlRequest");
  }
}
