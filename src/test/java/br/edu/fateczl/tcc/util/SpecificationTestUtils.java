package br.edu.fateczl.tcc.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Utilitário para executar uma {@link Specification} contra mocks de
 * Root/CriteriaQuery/CriteriaBuilder, permitindo verificar interações com o
 * {@link CriteriaBuilder} (ex.: {@code verify(cb).equal(...)}).
 *
 * Sem essa execução o lambda da Specification nunca roda - o repositório é
 * mockado e devolve a lista pronta - então mutantes do Pitest sobrevivem.
 *
 * Reutilizável para qualquer service que monte Specification dinamicamente
 * (atualmente TrajeService e MedidaService).
 */
public final class SpecificationTestUtils {

    private SpecificationTestUtils() {}

    public record CapturedSpec<T>(
            Root<T> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            Predicate predicate) {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CapturedSpec<T> invoke(Specification<T> spec) {
        Root<T> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path path = mock(Path.class);
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate predicateStub = mock(Predicate.class);

        lenient().when(root.get(anyString())).thenReturn(path);
        // cb.equal tem overload (Expression, Expression) e (Expression, Object).
        // O service usa o overload Object - stub explícito para esse.
        lenient().when(cb.equal(any(Expression.class), any(Object.class))).thenReturn(predicateStub);
        lenient().when(cb.lower(any(Expression.class))).thenReturn(lowerExpr);
        lenient().when(cb.like(any(Expression.class), anyString())).thenReturn(predicateStub);
        lenient().when(cb.or(any(Predicate[].class))).thenReturn(predicateStub);
        // Specification.and(...) usa CriteriaBuilder::and como method reference;
        // a resolução do Java escolhe o overload (Expression, Expression), não o varargs.
        lenient().when(cb.and(any(Expression.class), any(Expression.class))).thenReturn(predicateStub);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(predicateStub);

        Predicate result = spec.toPredicate(root, query, cb);
        return new CapturedSpec<>(root, query, cb, result);
    }
}
