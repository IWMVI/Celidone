package br.edu.fateczl.tcc.specification;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AluguelSpecification {

    private static final String STATUS = "status";
    private static final String OCASIAO = "ocasiao";
    private static final String DATA_RETIRADA = "dataRetirada";
    private static final String CLIENTE = "cliente";
    private static final String ID = "id";

    private AluguelSpecification() { }

    public static Specification<Aluguel> comStatus(StatusAluguel status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get(STATUS), status);
    }

    public static Specification<Aluguel> comClienteId(Long clienteId) {
        return (root, query, cb) ->
                clienteId == null ? null : cb.equal(root.get(CLIENTE).get(ID), clienteId);
    }

    public static Specification<Aluguel> comDataRetiradaEntre(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            if (inicio == null && fim == null) {
                return null;
            }
            if (inicio == null) {
                return cb.lessThanOrEqualTo(root.get(DATA_RETIRADA), fim);
            }
            if (fim == null) {
                return cb.greaterThanOrEqualTo(root.get(DATA_RETIRADA), inicio);
            }
            return cb.between(root.get(DATA_RETIRADA), inicio, fim);
        };
    }

    public static Specification<Aluguel> comOcasiao(TipoOcasiao ocasiao) {
        return (root, query, cb) ->
                ocasiao == null ? null : cb.equal(root.get(OCASIAO), ocasiao);
    }
}
