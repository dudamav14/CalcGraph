package calcgraph.persistence;

import calcgraph.model.Expressao;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ExpressaoRepository {

    public Expressao save(Expressao e) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (e.getId() == null) em.persist(e); else e = em.merge(e);
            em.getTransaction().commit();
            return e;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Optional<Expressao> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Expressao.class, id));
        } finally {
            em.close();
        }
    }

    public List<Expressao> listarRecentes(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Expressao e ORDER BY e.dataCriacao DESC",
                    Expressao.class
            ).setMaxResults(Math.max(1, limit)).getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Expressao e = em.find(Expressao.class, id);
            if (e != null) em.remove(e); // FK no banco cuida do favorito
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
