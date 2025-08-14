package calcgraph.persistence;

import calcgraph.model.Expressao;
import calcgraph.model.Favorito;

import javax.persistence.EntityManager;

public class PersistTeste {
    public static void main(String[] args) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            // 1) cria e persiste a expressão
            Expressao e = new Expressao();
            e.setEntrada("2+2");
            e.setResultado("4");
            e.setQuantidadeVariaveis(0);
            em.persist(e);

            // 2) (opcional) marca como favorito
            Favorito f = new Favorito();
            f.setExpressao(e);
            f.setApelido("Soma simples");
            f.setDescricao("Exemplo");
            em.persist(f);

            em.getTransaction().commit();
            System.out.println("OK! Expressao id = " + e.getId());
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) em.close();
            JPAUtil.close();
        }
    }
}
