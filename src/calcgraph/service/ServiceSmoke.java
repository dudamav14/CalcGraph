package calcgraph.service;

import calcgraph.model.Expressao;
import calcgraph.persistence.JPAUtil;

import java.util.List;

public class ServiceSmoke {
    public static void main(String[] args) {
        CalcService svc = new CalcService();

        // 1) Registrar uma expressão
        Expressao e = svc.registrarExpressao("3*3+1", "10", 0);
        System.out.println("Nova expressão id=" + e.getId());

        // 2) Marcar favorito
        svc.marcarFavorito(e.getId(), "Exemplo", "Teste favorito");
        System.out.println("Favorito criado/atualizado para a expressão " + e.getId());

        // 3) Listar histórico
        List<Expressao> ultimos = svc.historico(5);
        System.out.println("Histórico (top 5): " + ultimos.size() + " itens");

        // encerrar JPA
        JPAUtil.close();
    }
}
