package br.edu.fateczl.tcc.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class EnumUtils {

    private static final Map<Class<?>, Map<String, Enum<?>>> CACHE = new ConcurrentHashMap<>();

    private EnumUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & DisplayEnum> E fromValue(Class<E> enumType, String value) {
        if (value == null) {
            return null;
        }

        Map<String, Enum<?>> lookup = CACHE.computeIfAbsent(enumType, t -> buildLookup(t));
        E result = (E) lookup.get(value.toLowerCase(Locale.ROOT));

        if (result == null) {
            String validValues = Arrays.stream(enumType.getEnumConstants())
                    .map(DisplayEnum::getNomeExibicao)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Valor inválido: " + value + ". Valores válidos: " + validValues
            );
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Enum<?>> buildLookup(Class<?> enumType) {
        Map<String, Enum<?>> map = new ConcurrentHashMap<>();
        for (Object e : enumType.getEnumConstants()) {
            if (e instanceof DisplayEnum de) {
                Enum<?> enumValue = (Enum<?>) e;
                map.put(enumValue.name().toLowerCase(Locale.ROOT), enumValue);
                map.put(de.getNomeExibicao().toLowerCase(Locale.ROOT), enumValue);
            }
        }
        return Map.copyOf(map);
    }
}
