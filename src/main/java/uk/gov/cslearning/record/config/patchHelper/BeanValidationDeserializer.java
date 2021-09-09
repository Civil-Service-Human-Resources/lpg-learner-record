package uk.gov.cslearning.record.config.patchHelper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;

import javax.validation.*;
import java.io.IOException;
import java.util.Set;

class BeanValidationDeserializer extends BeanDeserializer {

    private final static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public BeanValidationDeserializer(BeanDeserializerBase src) {
        super(src);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext context) throws IOException {
        Object instance = super.deserialize(p, context);
        validate(instance);

        return instance;
    }

    private void validate(Object instance) {
        Set<ConstraintViolation<Object>> violations = validator.validate(instance);
        if (violations.size() > 0) {
            throw new ConstraintViolationException("JSON is not valid.", violations);
        }
    }
}