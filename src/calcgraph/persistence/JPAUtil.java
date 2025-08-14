package calcgraph.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JPAUtil {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("calcgraphPU");

    private JPAUtil() {}

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void close() {
        if (EMF.isOpen()) EMF.close();
    }
}
