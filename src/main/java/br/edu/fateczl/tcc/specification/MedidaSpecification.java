package br.edu.fateczl.tcc.specification;

import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.springframework.data.jpa.domain.Specification;

public class MedidaSpecification {

    private static final String CLIENTE = "cliente";
    private static final String ID = "id";
    private static final String SEXO = "sexo";


    private MedidaSpecification() { }

    public static Specification<Medida> comClienteId(Long clienteId) {
        return (root, query, cb) ->
                clienteId == null ? null : cb.equal(root.get(CLIENTE).get(ID), clienteId);
    }

    public static Specification<Medida> comSexo(SexoEnum sexo) {
        return (root, query, cb) ->
                sexo == null ? null : cb.equal(root.get(SEXO), sexo);
    }
}