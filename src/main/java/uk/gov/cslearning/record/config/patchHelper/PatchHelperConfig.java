package uk.gov.cslearning.record.config.patchHelper;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatchHelperConfig {

    @Bean
    public ObjectMapper patchMapper() {
        ObjectMapper patchObjectMapper = new ObjectMapper();
        SimpleModule validationModule = getValidationModule();
        patchObjectMapper.registerModule(validationModule);
        patchObjectMapper.registerModule(new JavaTimeModule());
        return patchObjectMapper;
    }

    private SimpleModule getValidationModule() {
        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (deserializer instanceof BeanDeserializer) {
                    return new BeanValidationDeserializer((BeanDeserializer) deserializer);
                }

                return deserializer;
            }
        });
        return validationModule;
    }
}
