package br.edu.fateczl.tcc.seeder;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.DevolucaoRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.seeder.data.AluguelSeedData;
import br.edu.fateczl.tcc.seeder.data.ClienteSeedData;
import br.edu.fateczl.tcc.seeder.data.DevolucaoSeedData;
import br.edu.fateczl.tcc.seeder.data.MedidaSeedData;
import br.edu.fateczl.tcc.seeder.data.MedidaSeedData.MedidaSeedSet;
import br.edu.fateczl.tcc.seeder.data.TrajeSeedData;
import br.edu.fateczl.tcc.service.ClienteService;
import br.edu.fateczl.tcc.service.MedidaService;
import br.edu.fateczl.tcc.service.TrajeService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final ClienteService clienteService;
    private final TrajeService trajeService;
    private final MedidaService medidaService;
    private final ClienteRepository clienteRepository;
    private final TrajeRepository trajeRepository;
    private final AluguelRepository aluguelRepository;
    private final DevolucaoRepository devolucaoRepository;

    public DatabaseSeeder(ClienteService clienteService,
                          TrajeService trajeService,
                          MedidaService medidaService,
                          ClienteRepository clienteRepository,
                          TrajeRepository trajeRepository,
                          AluguelRepository aluguelRepository,
                          DevolucaoRepository devolucaoRepository) {
        this.clienteService = clienteService;
        this.trajeService = trajeService;
        this.medidaService = medidaService;
        this.clienteRepository = clienteRepository;
        this.trajeRepository = trajeRepository;
        this.aluguelRepository = aluguelRepository;
        this.devolucaoRepository = devolucaoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        long existentes = clienteRepository.count();
        if (existentes > 0) {
            log.info("Seeder pulado: banco já populado ({} clientes)", existentes);
            return;
        }

        log.info("Iniciando seeder de dados de desenvolvimento...");

        ClienteSeedData.clientes().forEach(clienteService::criar);
        List<Cliente> clientes = clienteRepository.findAll().stream()
                .sorted(Comparator.comparing(Cliente::getId))
                .toList();

        TrajeSeedData.trajes().forEach(trajeService::criar);
        List<Traje> trajes = trajeRepository.findAll().stream()
                .sorted(Comparator.comparing(Traje::getId))
                .toList();

        MedidaSeedSet medidas = MedidaSeedData.medidas(clientes);
        medidas.masculinas().forEach(medidaService::criarMasculina);
        medidas.femininas().forEach(medidaService::criarFeminina);

        List<Aluguel> alugueis = AluguelSeedData.alugueis(clientes, trajes);
        aluguelRepository.saveAll(alugueis);

        List<Traje> trajesAlugados = alugueis.stream()
                .filter(a -> a.getStatus() == StatusAluguel.ATIVO)
                .flatMap(a -> a.getItens().stream())
                .map(ItemAluguel::getTraje)
                .distinct()
                .toList();
        trajesAlugados.forEach(t -> t.setStatus(StatusTraje.ALUGADO));
        trajeRepository.saveAll(trajesAlugados);

        List<Aluguel> concluidos = alugueis.stream()
                .filter(a -> a.getStatus() == StatusAluguel.CONCLUIDO)
                .toList();
        List<Devolucao> devolucoes = DevolucaoSeedData.devolucoes(concluidos);
        devolucaoRepository.saveAll(devolucoes);

        log.info("Seeder concluído: {} clientes, {} trajes, {} medidas, {} aluguéis ({} ATIVO + {} CONCLUIDO + {} CANCELADO), {} devoluções",
                clientes.size(),
                trajes.size(),
                medidas.masculinas().size() + medidas.femininas().size(),
                alugueis.size(),
                contar(alugueis, StatusAluguel.ATIVO),
                contar(alugueis, StatusAluguel.CONCLUIDO),
                contar(alugueis, StatusAluguel.CANCELADO),
                devolucoes.size());
    }

    private long contar(List<Aluguel> alugueis, StatusAluguel status) {
        return alugueis.stream().filter(a -> a.getStatus() == status).count();
    }
}
