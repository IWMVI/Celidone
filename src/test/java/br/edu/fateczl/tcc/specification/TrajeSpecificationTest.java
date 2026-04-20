package br.edu.fateczl.tcc.specification;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TrajeSpecification")
class TrajeSpecificationTest {

    @Mock
    private Root<Traje> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> path;

    @Nested
    @DisplayName("comStatus")
    class ComStatusTest {

        @Test
        void deveRetornarNullQuandoStatusForNull() {
            Specification<Traje> spec = TrajeSpecification.comStatus(null);

            Predicate result = spec.toPredicate(root, null, cb);

            assertNull(result);
        }

        @Test
        void deveRetornarPredicateQuandoStatusForInformado() {
            when(root.get("status")).thenReturn(path);
            when(cb.equal(path, StatusTraje.DISPONIVEL)).thenReturn(mock(Predicate.class));

            Specification<Traje> spec = TrajeSpecification.comStatus(StatusTraje.DISPONIVEL);

            assertNotNull(spec);
            Predicate result = spec.toPredicate(root, null, cb);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("comGenero")
    class ComGeneroTest {

        @Test
        void deveRetornarNullQuandoGeneroForNull() {
            Specification<Traje> spec = TrajeSpecification.comGenero(null);

            Predicate result = spec.toPredicate(root, null, cb);

            assertNull(result);
        }

        @Test
        void deveRetornarPredicateQuandoGeneroForInformado() {
            when(root.get("genero")).thenReturn(path);
            when(cb.equal(path, SexoEnum.MASCULINO)).thenReturn(mock(Predicate.class));

            Specification<Traje> spec = TrajeSpecification.comGenero(SexoEnum.MASCULINO);

            assertNotNull(spec);
            Predicate result = spec.toPredicate(root, null, cb);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("comTipo")
    class ComTipoTest {

        @Test
        void deveRetornarNullQuandoTipoForNull() {
            Specification<Traje> spec = TrajeSpecification.comTipo(null);

            Predicate result = spec.toPredicate(root, null, cb);

            assertNull(result);
        }

        @Test
        void deveRetornarPredicateQuandoTipoForInformado() {
            when(root.get("tipo")).thenReturn(path);
            when(cb.equal(path, TipoTraje.TERNO)).thenReturn(mock(Predicate.class));

            Specification<Traje> spec = TrajeSpecification.comTipo(TipoTraje.TERNO);

            assertNotNull(spec);
            Predicate result = spec.toPredicate(root, null, cb);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("comTamanho")
    class ComTamanhoTest {

        @Test
        void deveRetornarNullQuandoTamanhoForNull() {
            Specification<Traje> spec = TrajeSpecification.comTamanho(null);

            Predicate result = spec.toPredicate(root, null, cb);

            assertNull(result);
        }

        @Test
        void deveRetornarPredicateQuandoTamanhoForInformado() {
            when(root.get("tamanho")).thenReturn(path);
            when(cb.equal(path, TamanhoTraje.M)).thenReturn(mock(Predicate.class));

            Specification<Traje> spec = TrajeSpecification.comTamanho(TamanhoTraje.M);

            assertNotNull(spec);
            Predicate result = spec.toPredicate(root, null, cb);
            assertNotNull(result);
        }
    }
}
