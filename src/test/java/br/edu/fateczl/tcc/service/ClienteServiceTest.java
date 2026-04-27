package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static br.edu.fateczl.tcc.util.ClienteDataBuilder.CLIENTE_ID_ALTERNATIVO;
import static br.edu.fateczl.tcc.util.ClienteDataBuilder.CLIENTE_ID_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TFS — Teste Funcional Sistemático.
 *
 * Combina PCE (Particionamento em Classes de Equivalência) com AVL (Análise
 * do Valor Limite) de forma sistemática, seguindo o método proposto por
 * Delamaro/Maldonado/Jino ("Introdução ao Teste de Software"):
 *
 *   1) Identificar condições de entrada e seus domínios.
 *   2) Derivar classes de equivalência — válidas (V) e inválidas (I) — para
 *      cada condição.
 *   3) Identificar os valores limite de cada classe.
 *   4) Construir a matriz de casos de teste combinando:
 *        - Um caso "típico" com todas as classes válidas.
 *        - Casos nos limites das classes válidas (bordas inferiores/superiores).
 *        - Um caso para cada classe inválida, mantendo as demais entradas
 *          válidas — para isolar o efeito do defeito.
 *
 * =========================================================================
 * MATRIZ DE CLASSES DE EQUIVALÊNCIA (método criar)
 * =========================================================================
 *   Variável                  | Classes Válidas (V)         | Classes Inválidas (I)
 *   --------------------------|-----------------------------|------------------------------
 *   C1: nome                  | V1 não-blank                | I1 null/blank
 *   C2: cpfCnpj               | V2 não-blank                | I2 null/blank
 *   C3: email                 | V3 não-blank                | I3 null/blank
 *   C4: celular               | V4 não-blank                | I4 null/blank
 *   C5: cpfCnpj no banco      | V5 inexistente              | I5 já cadastrado
 *   C6: email no banco        | V6 inexistente              | I6 já cadastrado
 *   C7: resultado do save     | V7 sucesso                  | I7a  DIV causa "cpf"
 *                             |                             | I7a' DIV causa "cnpj"
 *                             |                             | I7b  DIV causa "email"
 *                             |                             | I7c  DIV causa genérica
 *                             |                             | I7d  DIV sem cause
 *                             |                             | I7e  DIV com cause de msg null
 *
 * VALORES LIMITE RELEVANTES:
 *   - nome/cpf/email/celular: string em branco ("") e null — ambos caem na
 *     classe inválida via isBlank().
 *   - mensagem da causa do DIV: conteúdo com "cpf", "cnpj", "email" ou nada
 *     disso (ou causa/mensagem ausentes) — borda do matching por substring.
 *
 * CASOS DE TESTE DERIVADOS (criar):
 *   CT1  — todas V, valores típicos                           → sucesso
 *   CT2  — I1 isolada (nome em branco)                        → BusinessException "Nome é obrigatório"
 *   CT3  — I2 isolada (cpfCnpj em branco)                     → BusinessException "CPF é obrigatório"
 *   CT4  — I3 isolada (email em branco)                       → BusinessException "Email é obrigatório"
 *   CT5  — I4 isolada (celular em branco)                     → BusinessException "Telefone é obrigatório"
 *   CT6  — I5 isolada (CPF já cadastrado)                     → BusinessException "CPF ou CNPJ já cadastrado"
 *   CT7  — I6 isolada (email já cadastrado)                   → BusinessException "Email já cadastrado"
 *   CT8  — I7a  isolada (DIV causa "cpf")                     → BusinessException "CPF ou CNPJ já cadastrado"
 *   CT8b — I7a' isolada (DIV causa "cnpj", sem "cpf")         → BusinessException "CPF ou CNPJ já cadastrado"
 *   CT9  — I7b  isolada (DIV causa "email")                   → BusinessException "Email já cadastrado"
 *   CT10 — I7c isolada (DIV causa genérica)                   → BusinessException "Violação de integridade."
 *   CT11 — I7d isolada (DIV sem cause)                        → BusinessException "Violação de integridade."
 *   CT12 — I7e isolada (DIV com cause de msg null)            → BusinessException "Violação de integridade."
 *
 * =========================================================================
 * MATRIZ (método atualizar) — acrescenta três variáveis:
 *   C8: existência do cliente: V8 existe / I8 não existe
 *   C9: cpf novo vs. atual:    V9 igual OU diferente-e-livre / I9 diferente-e-duplicado
 *   C10: email novo vs. atual: V10 igual OU diferente-e-livre / I10 diferente-e-duplicado
 *
 * CASOS DE TESTE (atualizar):
 *   CT13 — todas V (cpf e email mudam e ambos livres)         → sucesso
 *   CT14 — V borda: cpf e email iguais ao atual               → sucesso, findByCpfCnpj/findByEmail nunca chamados
 *   CT15 — I8 isolada (cliente inexistente)                   → ResourceNotFoundException
 *   CT16 — I9 isolada (novo CPF pertence a outro)             → BusinessException "CPF ou CNPJ já cadastrado"
 *   CT17 — I10 isolada (novo email pertence a outro)          → BusinessException "Email já cadastrado"
 *   CT18 — I1 reaplicada em atualizar (nome em branco)        → BusinessException "Nome é obrigatório"
 *   CT19 — V: só email muda, cpf mantém                       → chama findByEmail, nunca findByCpfCnpj
 *
 * =========================================================================
 * Operações de leitura/remoção/recuperação possuem condições simples de
 * entrada (existência do id, ativo/!ativo, termo de busca preenchido/vazio)
 * — tratadas como V/I diretas em CT20..CT37.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - ClienteService (Teste Funcional Sistemático)")
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = ClienteDataBuilder.umCliente().buildEntity();
    }

    private void stubarCaminhoFelizCriar() {
        when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenReturn(cliente);
    }

    // =========================================================
    // CRIAR — CT1..CT12
    // =========================================================
    @Nested
    @DisplayName("Criar Cliente — matriz TFS")
    class Criar {

        @Test
        @DisplayName("CT1 — todas as classes VÁLIDAS, valores típicos")
        void ct1_deve_criar_quando_todasClassesValidasEmValoresTipicos() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            stubarCaminhoFelizCriar();

            ClienteResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(ClienteDataBuilder.NOME_DEFAULT, response.nome());
            assertEquals(ClienteDataBuilder.CPF_DEFAULT, response.cpfCnpj());
            assertEquals(ClienteDataBuilder.EMAIL_DEFAULT, response.email());
            assertEquals(ClienteDataBuilder.CELULAR_DEFAULT, response.celular());
            assertEquals("MASCULINO", response.sexo());
            verify(repository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT2 — I1 isolada: nome em branco, demais VÁLIDAS")
        void ct2_deve_lancarBusinessException_quando_apenasNomeEmBranco() {
            ClienteRequest request = ClienteDataBuilder.umCliente().semNome().buildRequest();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Nome é obrigatório", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT3 — I2 isolada: cpfCnpj em branco, demais VÁLIDAS")
        void ct3_deve_lancarBusinessException_quando_apenasCpfEmBranco() {
            ClienteRequest request = ClienteDataBuilder.umCliente().semCpfCnpj().buildRequest();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("CPF é obrigatório", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT4 — I3 isolada: email em branco, demais VÁLIDAS")
        void ct4_deve_lancarBusinessException_quando_apenasEmailEmBranco() {
            ClienteRequest request = ClienteDataBuilder.umCliente().semEmail().buildRequest();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Email é obrigatório", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT5 — I4 isolada: celular em branco, demais VÁLIDAS")
        void ct5_deve_lancarBusinessException_quando_apenasCelularEmBranco() {
            ClienteRequest request = ClienteDataBuilder.umCliente().semCelular().buildRequest();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Telefone é obrigatório", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT6 — I5 isolada: CPF já cadastrado, demais VÁLIDAS")
        void ct6_deve_lancarBusinessException_quando_apenasCpfJaCadastrado() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("CPF ou CNPJ já cadastrado", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT7 — I6 isolada: email já cadastrado, demais VÁLIDAS")
        void ct7_deve_lancarBusinessException_quando_apenasEmailJaCadastrado() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Email já cadastrado", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT8 — I7a isolada na borda: DIV com causa contendo 'cpf'")
        void ct8_deve_traduzirParaCpfDuplicado_quando_saveLancaDivComCausaCpf() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            DataIntegrityViolationException div = new DataIntegrityViolationException(
                    "erro", new SQLIntegrityConstraintViolationException("Duplicate entry for cpf"));
            when(repository.save(any(Cliente.class))).thenThrow(div);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("CPF ou CNPJ já cadastrado", ex.getMessage());
        }

        @Test
        @DisplayName("CT8b — I7a' isolada na borda: DIV com causa contendo 'cnpj' (sem 'cpf')")
        void ct8b_deve_traduzirParaCpfDuplicado_quando_saveLancaDivComCausaCnpj() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            DataIntegrityViolationException div = new DataIntegrityViolationException(
                    "erro", new SQLIntegrityConstraintViolationException("Duplicate entry for cnpj"));
            when(repository.save(any(Cliente.class))).thenThrow(div);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("CPF ou CNPJ já cadastrado", ex.getMessage());
        }

        @Test
        @DisplayName("CT9 — I7b isolada na borda: DIV com causa contendo 'email'")
        void ct9_deve_traduzirParaEmailDuplicado_quando_saveLancaDivComCausaEmail() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            DataIntegrityViolationException div = new DataIntegrityViolationException(
                    "erro", new SQLIntegrityConstraintViolationException("Duplicate entry for email"));
            when(repository.save(any(Cliente.class))).thenThrow(div);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Email já cadastrado", ex.getMessage());
        }

        @Test
        @DisplayName("CT10 — I7c isolada na borda: DIV com causa genérica")
        void ct10_deve_traduzirParaMensagemGenerica_quando_saveLancaDivComCausaGenerica() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            DataIntegrityViolationException div = new DataIntegrityViolationException(
                    "erro", new SQLIntegrityConstraintViolationException("Unknown constraint"));
            when(repository.save(any(Cliente.class))).thenThrow(div);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Erro ao salvar cliente. Violação de integridade.", ex.getMessage());
        }

        @Test
        @DisplayName("CT11 — I7d isolada: DIV sem cause")
        void ct11_deve_traduzirParaMensagemGenerica_quando_saveLancaDivSemCause() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenThrow(new DataIntegrityViolationException("erro"));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Erro ao salvar cliente. Violação de integridade.", ex.getMessage());
        }

        @Test
        @DisplayName("CT12 — I7e isolada: DIV com cause de mensagem null")
        void ct12_deve_traduzirParaMensagemGenerica_quando_saveLancaDivComCauseDeMsgNula() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            DataIntegrityViolationException div = new DataIntegrityViolationException(
                    "erro", new SQLIntegrityConstraintViolationException((String) null));
            when(repository.save(any(Cliente.class))).thenThrow(div);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Erro ao salvar cliente. Violação de integridade.", ex.getMessage());
        }
    }

    // =========================================================
    // ATUALIZAR — CT13..CT19
    // =========================================================
    @Nested
    @DisplayName("Atualizar Cliente — matriz TFS")
    class Atualizar {

        @Test
        @DisplayName("CT13 — todas VÁLIDAS: cpf e email novos e livres")
        void ct13_deve_atualizar_quando_todasClassesValidas() {
            ClienteRequest request = ClienteDataBuilder.umCliente()
                    .comCpfCnpj("98765432100")
                    .comEmail("joao.novo@email.com")
                    .buildRequest();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(repository.findByCpfCnpj("98765432100")).thenReturn(Optional.empty());
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            ClienteResponse response = service.atualizar(CLIENTE_ID_DEFAULT, request);

            assertNotNull(response);
            assertEquals("98765432100", response.cpfCnpj());
            assertEquals("joao.novo@email.com", response.email());
            verify(repository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT14 — V borda: cpf e email IGUAIS ao atual (não consulta unicidade)")
        void ct14_deve_atualizar_quando_cpfEEmailMantidos() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            ClienteResponse response = service.atualizar(CLIENTE_ID_DEFAULT, request);

            assertNotNull(response);
            verify(repository, never()).findByCpfCnpj(anyString());
            verify(repository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("CT15 — I8 isolada: cliente inexistente")
        void ct15_deve_lancarResourceNotFound_quando_apenasClienteInexistente() {
            ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.atualizar(99L, request));
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT16 — I9 isolada: novo CPF pertence a OUTRO cliente")
        void ct16_deve_lancarBusinessException_quando_apenasCpfPertenceAOutro() {
            ClienteRequest request = ClienteDataBuilder.umCliente()
                    .comCpfCnpj("98765432100")
                    .buildRequest();
            Cliente outro = ClienteDataBuilder.umOutroCliente(CLIENTE_ID_ALTERNATIVO);
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(repository.findByCpfCnpj("98765432100")).thenReturn(Optional.of(outro));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(CLIENTE_ID_DEFAULT, request));
            assertEquals("CPF ou CNPJ já cadastrado", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT17 — I10 isolada: novo email pertence a OUTRO cliente")
        void ct17_deve_lancarBusinessException_quando_apenasEmailPertenceAOutro() {
            ClienteRequest request = ClienteDataBuilder.umCliente()
                    .comEmail("joao.novo@email.com")
                    .buildRequest();
            Cliente outro = ClienteDataBuilder.umOutroCliente(CLIENTE_ID_ALTERNATIVO);
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.of(outro));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(CLIENTE_ID_DEFAULT, request));
            assertEquals("Email já cadastrado", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT18 — I1 reaplicada em atualizar: nome em branco")
        void ct18_deve_lancarBusinessException_quando_nomeEmBrancoAoAtualizar() {
            ClienteRequest request = ClienteDataBuilder.umCliente().semNome().buildRequest();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(CLIENTE_ID_DEFAULT, request));
            assertEquals("Nome é obrigatório", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT19 — V: só email muda, cpf mantém (consulta apenas findByEmail)")
        void ct19_deve_consultarApenasEmail_quando_soEmailMuda() {
            ClienteRequest request = ClienteDataBuilder.umCliente()
                    .comEmail("joao.novo@email.com")
                    .buildRequest();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(cliente);

            service.atualizar(CLIENTE_ID_DEFAULT, request);

            verify(repository, never()).findByCpfCnpj(anyString());
            verify(repository).findByEmail("joao.novo@email.com");
        }
    }

    // =========================================================
    // BUSCAR POR ID — CT20, CT21, CT22
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID — matriz TFS")
    class BuscarPorId {

        @Test
        @DisplayName("CT20 — V: id existe e cliente ativo")
        void ct20_deve_retornar_quando_idExisteEAtivo() {
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            ClienteResponse response = service.buscarPorId(CLIENTE_ID_DEFAULT);

            assertNotNull(response);
            assertEquals(ClienteDataBuilder.NOME_DEFAULT, response.nome());
        }

        @Test
        @DisplayName("CT21 — I11 isolada: id inexistente")
        void ct21_deve_lancarResourceNotFound_quando_idInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
        }

        @Test
        @DisplayName("CT22 — I12 isolada: cliente existe mas está inativo")
        void ct22_deve_lancarResourceNotFound_quando_clienteInativo() {
            Cliente inativo = ClienteDataBuilder.umCliente().ativo(false).buildEntity();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(inativo));

            assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(CLIENTE_ID_DEFAULT));
        }
    }

    // =========================================================
    // LISTAR E BUSCAR COM FILTRO — CT23..CT29
    // =========================================================
    @Nested
    @DisplayName("Listar / Buscar com filtro — matriz TFS")
    class ListarEBuscar {

        @Test
        @DisplayName("CT23 — V típico: listar com 1+ clientes")
        void ct23_deve_retornarLista_quando_existemClientes() {
            when(repository.findAll()).thenReturn(List.of(cliente));

            List<ClienteResponse> responses = service.listar();

            assertEquals(1, responses.size());
            assertEquals(ClienteDataBuilder.NOME_DEFAULT, responses.getFirst().nome());
        }

        @Test
        @DisplayName("CT24 — V borda: listar sem nenhum cliente")
        void ct24_deve_retornarListaVazia_quando_nenhumCliente() {
            when(repository.findAll()).thenReturn(List.of());

            List<ClienteResponse> responses = service.listar();

            assertTrue(responses.isEmpty());
        }

        @Test
        @DisplayName("CT25 — V: buscarComFiltro com termo preenchido chama buscarPorTermo")
        void ct25_deve_usarBuscarPorTermo_quando_buscaPreenchida() {
            when(repository.buscarPorTermo("joao")).thenReturn(List.of(cliente));

            List<ClienteResponse> responses = service.buscarComFiltro("joao");

            assertEquals(1, responses.size());
            verify(repository, never()).findAll();
        }

        @Test
        @DisplayName("CT26 — V borda: buscarComFiltro(null) chama findAll")
        void ct26_deve_usarFindAll_quando_buscaNula() {
            when(repository.findAll()).thenReturn(List.of(cliente));

            List<ClienteResponse> responses = service.buscarComFiltro(null);

            assertEquals(1, responses.size());
            verify(repository, never()).buscarPorTermo(anyString());
        }

        @Test
        @DisplayName("CT27 — V borda: buscarComFiltro(\"\") chama findAll")
        void ct27_deve_usarFindAll_quando_buscaVazia() {
            when(repository.findAll()).thenReturn(List.of(cliente));

            List<ClienteResponse> responses = service.buscarComFiltro("");

            assertEquals(1, responses.size());
            verify(repository, never()).buscarPorTermo(anyString());
        }

        @Test
        @DisplayName("CT28 — V: buscarComFiltroPaginado com termo chama buscarPorTermoPaginado")
        void ct28_deve_usarBuscarPorTermoPaginado_quando_buscaPreenchida() {
            Page<Cliente> page = new PageImpl<>(List.of(cliente));
            when(repository.buscarPorTermoPaginado(eq("joao"), any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.buscarComFiltroPaginado("joao", 0, 10);

            assertEquals(1, responses.getTotalElements());
            verify(repository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("CT29 — V borda: buscarComFiltroPaginado(null) chama findAll(pageable)")
        void ct29_deve_usarFindAllPaginado_quando_buscaNula() {
            Page<Cliente> page = new PageImpl<>(List.of(cliente));
            when(repository.findAll(any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.buscarComFiltroPaginado(null, 0, 10);

            assertEquals(1, responses.getTotalElements());
            verify(repository, never()).buscarPorTermoPaginado(anyString(), any(PageRequest.class));
        }
    }

    // =========================================================
    // DELETAR — CT30, CT31, CT32
    // =========================================================
    @Nested
    @DisplayName("Deletar Cliente — matriz TFS")
    class Deletar {

        @Test
        @DisplayName("CT30 — V: cliente ativo → desativa e salva")
        void ct30_deve_desativar_quando_clienteAtivo() {
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            service.deletar(CLIENTE_ID_DEFAULT);

            assertFalse(cliente.getAtivo());
            verify(repository).save(cliente);
        }

        @Test
        @DisplayName("CT31 — I12 isolada: cliente já inativo")
        void ct31_deve_lancarBusinessException_quando_clienteJaInativo() {
            Cliente inativo = ClienteDataBuilder.umCliente().ativo(false).buildEntity();
            when(repository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(inativo));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.deletar(CLIENTE_ID_DEFAULT));
            assertEquals("Cliente já foi deletado", ex.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("CT32 — I11 isolada: id inexistente")
        void ct32_deve_lancarResourceNotFound_quando_idInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.deletar(99L));
            verify(repository, never()).save(any(Cliente.class));
        }
    }

    // =========================================================
    // LISTAR EXCLUÍDOS E RECUPERAR — CT33..CT37
    // =========================================================
    @Nested
    @DisplayName("Listar Excluídos e Recuperar — matriz TFS")
    class ListarExcluidosERecuperar {

        @Test
        @DisplayName("CT33 — V típico: listar excluídos com 1+")
        void ct33_deve_retornarLista_quando_existemExcluidos() {
            Cliente excluido = ClienteDataBuilder.umCliente().ativo(false).buildEntity();
            when(repository.findAllExcluidos()).thenReturn(List.of(excluido));

            List<ClienteResponse> responses = service.listarExcluidos();

            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("CT34 — V borda: nenhum excluído")
        void ct34_deve_retornarListaVazia_quando_nenhumExcluido() {
            when(repository.findAllExcluidos()).thenReturn(List.of());

            List<ClienteResponse> responses = service.listarExcluidos();

            assertTrue(responses.isEmpty());
        }

        @Test
        @DisplayName("CT35 — V: listar excluídos paginado")
        void ct35_deve_retornarPagina_quando_listarExcluidosPaginado() {
            Cliente excluido = ClienteDataBuilder.umCliente().ativo(false).buildEntity();
            Page<Cliente> page = new PageImpl<>(List.of(excluido));
            when(repository.findAllExcluidos(any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.listarExcluidosPaginado(0, 10);

            assertEquals(1, responses.getTotalElements());
        }

        @Test
        @DisplayName("CT36 — V: recuperar cliente excluído reativa ativo=true")
        void ct36_deve_recuperar_quando_clienteExcluidoEncontrado() {
            Cliente excluido = ClienteDataBuilder.umCliente().ativo(false).buildEntity();
            when(repository.findExcluidoById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(excluido));

            ClienteResponse response = service.recuperar(CLIENTE_ID_DEFAULT);

            assertNotNull(response);
            assertTrue(excluido.getAtivo());
        }

        @Test
        @DisplayName("CT37 — I13 isolada: recuperar id inexistente")
        void ct37_deve_lancarResourceNotFound_quando_excluidoInexistente() {
            when(repository.findExcluidoById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.recuperar(99L));
        }
    }
}
