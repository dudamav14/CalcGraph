package calcgraph.persistence;

import javax.persistence.EntityManager;

public class DBPing {
    public static void main(String[] args) {
        System.out.println("Abrindo EntityManager...");
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("Conectado! Resultado do SELECT 1 = " + result);
        } catch (Exception e) {
            System.err.println("Falha ao conectar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) em.close();
            JPAUtil.close();
        }
    }
}
