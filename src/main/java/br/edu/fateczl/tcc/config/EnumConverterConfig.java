package br.edu.fateczl.tcc.config;

import br.edu.fateczl.tcc.enums.DisplayEnum;
import br.edu.fateczl.tcc.enums.EnumUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new DisplayEnumConverterFactory());
    }

    private static final class DisplayEnumConverterFactory
            implements ConverterFactory<String, Enum<?>> {

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
            if (!DisplayEnum.class.isAssignableFrom(targetType)) {
                return null;
            }
            Class enumType = targetType;
            return source -> (T) EnumUtils.fromValue(enumType, source);
        }
    }
}
