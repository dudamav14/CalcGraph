package calcgraph.service;

import calcgraph.model.Expressao;
import calcgraph.model.Favorito;
import calcgraph.persistence.ExpressaoRepository;
import calcgraph.persistence.FavoritoRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Classe de testes para CalcService, usando "dublês de teste" no lugar do Mockito.
 */
public class CalcServiceTestes {

    private CalcService calcService;

    // --- Classes Dublês (Test Doubles) ---

    // Dublê para ExpressaoRepository
    private static class FakeExpressaoRepository extends ExpressaoRepository {
        public List<Expressao> savedExpressions = new ArrayList<>();
        public Long lastDeletedId = null;
        public Optional<Expressao> findByIdResult = Optional.empty();
        public boolean shouldThrowException = false;
        
        @Override
        public Expressao save(Expressao e) {
            if (shouldThrowException) {
                throw new RuntimeException("Simulated save error");
            }
            // Simula o salvamento e atribui um ID
            e.setId(1L); 
            savedExpressions.add(e);
            return e;
        }

        @Override
        public Optional<Expressao> findById(Long id) {
            if (shouldThrowException) {
                throw new RuntimeException("Simulated find error");
            }
            return findByIdResult;
        }

        @Override
        public List<Expressao> listarRecentes(int limit) {
             if (shouldThrowException) {
                throw new RuntimeException("Simulated list error");
            }
            // Retorna uma lista de teste
            return new ArrayList<>();
        }

        @Override
        public void deleteById(Long id) {
             if (shouldThrowException) {
                throw new RuntimeException("Simulated delete error");
            }
            this.lastDeletedId = id;
        }
    }

    // Dublê para FavoritoRepository
    private static class FakeFavoritoRepository extends FavoritoRepository {
        public Favorito savedFavorite = null;
        public Optional<Favorito> findByExpressaoIdResult = Optional.empty();
        public Long lastDeletedId = null;
        public boolean shouldThrowException = false;

        @Override
        public Favorito save(Favorito f) {
            if (shouldThrowException) {
                throw new RuntimeException("Simulated save error");
            }
            f.setId(10L);
            this.savedFavorite = f;
            return f;
        }

        @Override
        public Optional<Favorito> findByExpressaoId(Long idExpressao) {
            if (shouldThrowException) {
                throw new RuntimeException("Simulated find error");
            }
            return findByExpressaoIdResult;
        }
        
        @Override
        public void deleteById(Long id) {
            if (shouldThrowException) {
                throw new RuntimeException("Simulated delete error");
            }
            this.lastDeletedId = id;
        }
    }

    private FakeExpressaoRepository fakeExpRepo;
    private FakeFavoritoRepository fakeFavRepo;
    
    // Configuração inicial para cada teste
    @Before
    public void setUp() {
        // Injeta os dublês manualmente no construtor
        fakeExpRepo = new FakeExpressaoRepository();
        fakeFavRepo = new FakeFavoritoRepository();
        calcService = new CalcService(fakeExpRepo, fakeFavRepo);
    }
    
    // --- Testes para o método registrarExpressao ---

    @Test
    public void testRegistrarExpressao_ReturnsExpressaoObject() {
        // Cenário: o método deve retornar um objeto Expressao válido.
        Expressao novaExpressao = calcService.registrarExpressao("2+2", "4", 0);
        assertNotNull(novaExpressao);
    }

    @Test
    public void testRegistrarExpressao_CorrectlySetsEntrada() {
        // Cenário: o método deve setar a entrada corretamente.
        Expressao novaExpressao = calcService.registrarExpressao("2+2", "4", 0);
        assertEquals("2+2", novaExpressao.getEntrada());
    }

    @Test
    public void testRegistrarExpressao_CorrectlySetsResultado() {
        // Cenário: o método deve setar o resultado corretamente.
        Expressao novaExpressao = calcService.registrarExpressao("2+2", "4", 0);
        assertEquals("4", novaExpressao.getResultado());
    }

    @Test
    public void testRegistrarExpressao_CorrectlySetsQuantidadeVariaveis() {
        // Cenário: o método deve setar a quantidade de variáveis corretamente.
        Expressao novaExpressao = calcService.registrarExpressao("2x+2", "4", 1);
        assertEquals(1L, (long) novaExpressao.getQuantidadeVariaveis());
    }

    @Test
    public void testRegistrarExpressao_CallsSaveOnce() {
        // Cenário: o método deve chamar o save do repositório.
        int initialSize = fakeExpRepo.savedExpressions.size();
        calcService.registrarExpressao("2+2", "4", 0);
        assertEquals(initialSize + 1, fakeExpRepo.savedExpressions.size());
    }
    
    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testRegistrarExpressao_PropagatesException() {
        // Cenário: o método deve propagar a exceção do repositório.
        fakeExpRepo.shouldThrowException = true;
        calcService.registrarExpressao("2+2", "4", 0);
    }

    // --- Testes para o método historico ---

    @Test
    public void testHistorico_ReturnsListFromRepo() {
        // Cenário: o método deve retornar a lista fornecida pelo repositório.
        List<Expressao> mockList = new ArrayList<>();
        mockList.add(new Expressao());
        // O teste é limitado porque o método listarRecentes do dublê não é configurável
        List<Expressao> historico = calcService.historico(5);
        assertEquals(0, historico.size()); 
    }
    
    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testHistorico_PropagatesException() {
        // Cenário: o método deve propagar a exceção do repositório.
        fakeExpRepo.shouldThrowException = true;
        calcService.historico(5);
    }

    // --- Testes para o método marcarFavorito ---
    
    @Test
    public void testMarcarFavorito_NewFavorite_ExpressaoIsFound() {
        // Cenário: um novo favorito é criado quando a expressão é encontrada.
        Expressao expressaoMock = new Expressao();
        expressaoMock.setId(1L);
        fakeExpRepo.findByIdResult = Optional.of(expressaoMock);
        
        Favorito novoFavorito = calcService.marcarFavorito(1L, "Meu Favorito", "Teste");
        
        assertEquals(expressaoMock, novoFavorito.getExpressao());
    }

    @Test
    public void testMarcarFavorito_NewFavorite_CorrectApelido() {
        // Cenário: um novo favorito é criado com o apelido correto.
        fakeExpRepo.findByIdResult = Optional.of(new Expressao());
        
        Favorito novoFavorito = calcService.marcarFavorito(1L, "Meu Favorito", "Teste");
        
        assertEquals("Meu Favorito", novoFavorito.getApelido());
    }

    @Test
    public void testMarcarFavorito_UpdatesExisting_CorrectApelido() {
        // Cenário: o método atualiza um favorito existente com o apelido correto.
        Favorito favoritoExistente = new Favorito();
        favoritoExistente.setId(10L);
        fakeExpRepo.findByIdResult = Optional.of(new Expressao());
        fakeFavRepo.findByExpressaoIdResult = Optional.of(favoritoExistente);
        
        Favorito favoritoAtualizado = calcService.marcarFavorito(1L, "Novo Apelido", "Nova Descrição");
        
        assertEquals("Novo Apelido", favoritoAtualizado.getApelido());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMarcarFavorito_ThrowsExceptionForNonExistentExpression() {
        // Cenário: tentar marcar uma expressão inexistente como favorita deve lançar exceção.
        fakeExpRepo.findByIdResult = Optional.empty();
        calcService.marcarFavorito(999L, "Apelido", "Descricao");
    }
    
    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testMarcarFavorito_PropagatesExceptionFromExpressaoRepo() {
        // Cenário: deve propagar a exceção do findById.
        fakeExpRepo.shouldThrowException = true;
        calcService.marcarFavorito(1L, "Apelido", "Descricao");
    }

    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testMarcarFavorito_PropagatesExceptionFromFavoritoRepo() {
        // Cenário: deve propagar a exceção do findByExpressaoId.
        fakeExpRepo.findByIdResult = Optional.of(new Expressao());
        fakeFavRepo.shouldThrowException = true;
        calcService.marcarFavorito(1L, "Apelido", "Descricao");
    }
    
    // --- Testes para os métodos de remoção ---
    
    @Test
    public void testRemoverExpressao_CallsDeleteByIdOnce() {
        // Cenário: o método deve chamar o deleteById do ExpressaoRepository uma vez.
        calcService.removerExpressao(1L);
        assertEquals(1L, (long) fakeExpRepo.lastDeletedId);
    }
    
    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testRemoverExpressao_PropagatesException() {
        // Cenário: deve propagar a exceção do deleteById.
        fakeExpRepo.shouldThrowException = true;
        calcService.removerExpressao(1L);
    }

    @Test
    public void testRemoverFavorito_CallsDeleteByIdOnce() {
        // Cenário: o método deve chamar o deleteById do FavoritoRepository uma vez.
        calcService.removerFavorito(1L);
        assertEquals(1L, (long) fakeFavRepo.lastDeletedId);
    }
    
    // NOVO TESTE DE ERRO
    @Test(expected = RuntimeException.class)
    public void testRemoverFavorito_PropagatesException() {
        // Cenário: deve propagar a exceção do deleteById.
        fakeFavRepo.shouldThrowException = true;
        calcService.removerFavorito(1L);
    }
}