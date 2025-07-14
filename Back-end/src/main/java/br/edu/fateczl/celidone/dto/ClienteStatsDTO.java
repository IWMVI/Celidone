package br.edu.fateczl.celidone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estatísticas dos clientes
 * Segue o princípio de separação de responsabilidades
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteStatsDTO {

    private Long totalClientes;
    private Long clientesHoje;
    private Long clientesMes;
    private Long clientesAtivos;
    private Long clientesInativos;
    private Long novos7Dias;
    private Long clientesPessoaFisica;
    private Long clientesPessoaJuridica;
    private Double mediaIdade;
    private String cidadeMaisClientes;
    private Long clientesCidadeMaisClientes;
} 