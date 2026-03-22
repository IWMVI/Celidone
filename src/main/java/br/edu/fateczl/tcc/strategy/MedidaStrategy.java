package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.enums.SexoEnum;

public interface MedidaStrategy {

    /**
     * Cria uma medida a partir do DTO apropriado
     * @param dto DTO específico (Feminina ou Masculina)
     * @param cliente Cliente associado
     * @return Entidade Medida
     */
    Medida criar(Object dto, Cliente cliente);

    /**
     * Retorna o tipo de sexo que essa strategy atende
     */
    SexoEnum getTipo();
}