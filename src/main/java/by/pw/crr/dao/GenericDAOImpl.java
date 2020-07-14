package by.pw.crr.dao;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public abstract class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {
    private final EntityManager em = Persistence.createEntityManagerFactory("tg-bot").createEntityManager();
    private final Class<T> entityClass;

    public GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public synchronized EntityManager getEM() {
        return this.em;
    }

    @Override
    public void create(T t) {
        this.getEM().getTransaction().begin();
        this.getEM().persist(t);
        this.getEM().getTransaction().commit();
    }

    @Override
    public T findByID(ID id) {
        return this.getEM().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> c =
                this.getEM().getCriteriaBuilder().createQuery(entityClass);
        c.select(c.from(entityClass));
        return this.getEM().createQuery(c).getResultList();
    }

    @Override
    public void update(T t) {
        this.getEM().getTransaction().begin();
        this.getEM().merge(t);
        this.getEM().getTransaction().commit();
    }

    @Override
    public void delete(T t) {
        this.getEM().remove(t);
    }
}
