package calcgraph.persistence;

import calcgraph.model.Favorito;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class FavoritoRepository {

    public Favorito save(Favorito f) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (f.getId() == null) em.persist(f); else f = em.merge(f);
            em.getTransaction().commit();
            return f;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Optional<Favorito> findByExpressaoId(Long idExpressao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Favorito> list = em.createQuery(
                    "SELECT f FROM Favorito f WHERE f.expressao.id = :id",
                    Favorito.class
            )
            .setParameter("id", idExpressao)
            .setMaxResults(1)
            .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }


    public List<Favorito> listAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT f FROM Favorito f ORDER BY f.apelido ASC",
                    Favorito.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Favorito f = em.find(Favorito.class, id);
            if (f != null) em.remove(f);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
