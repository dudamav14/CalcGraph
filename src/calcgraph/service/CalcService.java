package calcgraph.service;

import calcgraph.model.Expressao;
import calcgraph.model.Favorito;
import calcgraph.persistence.ExpressaoRepository;
import calcgraph.persistence.FavoritoRepository;

import java.util.List;
import java.util.Optional;

public class CalcService {
    private final ExpressaoRepository expRepo = new ExpressaoRepository();
    private final FavoritoRepository favRepo = new FavoritoRepository();

    /** Registra uma expressão no histórico */
    public Expressao registrarExpressao(String entrada, String resultado, int qtdVars) {
        Expressao e = new Expressao();
        e.setEntrada(entrada);
        e.setResultado(resultado);
        e.setQuantidadeVariaveis(qtdVars);
        return expRepo.save(e);
    }

    /** Lista últimas N expressões */
    public List<Expressao> historico(int limit) {
        return expRepo.listarRecentes(limit);
    }

    /** Cria/atualiza favorito para uma expressão */
    public Favorito marcarFavorito(Long idExpressao, String apelido, String descricao) {
        Optional<Expressao> opt = expRepo.findById(idExpressao);
        if (!opt.isPresent()) throw new IllegalArgumentException("Expressão não encontrada: " + idExpressao);

        Optional<Favorito> atual = favRepo.findByExpressaoId(idExpressao);
        Favorito f = atual.isPresent() ? atual.get() : new Favorito();
        f.setExpressao(opt.get());
        f.setApelido(apelido);
        f.setDescricao(descricao);
        return favRepo.save(f);
    }

    public void removerExpressao(Long id) { expRepo.deleteById(id); }

    public void removerFavorito(Long idFav) { favRepo.deleteById(idFav); }
}
