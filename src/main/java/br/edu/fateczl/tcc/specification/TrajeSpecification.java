package br.edu.fateczl.tcc.specification;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import org.springframework.data.jpa.domain.Specification;

public class TrajeSpecification {

    private static final String STATUS = "status";
    private static final String GENERO = "genero";
    private static final String TIPO = "tipo";
    private static final String TAMANHO = "tamanho";

    private TrajeSpecification () { }

    public static Specification<Traje> comStatus(StatusTraje status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get(STATUS), status);
    }

    public static Specification<Traje> comGenero(SexoEnum genero) {
        return (root, query, cb) ->
                genero == null ? null : cb.equal(root.get(GENERO), genero);
    }

    public static Specification<Traje> comTipo(TipoTraje tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get(TIPO), tipo);
    }

    public static Specification<Traje> comTamanho(TamanhoTraje tamanho) {
        return (root, query, cb) ->
                tamanho == null ? null : cb.equal(root.get(TAMANHO), tamanho);
    }
}