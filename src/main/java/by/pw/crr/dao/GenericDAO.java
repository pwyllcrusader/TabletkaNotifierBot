package by.pw.crr.dao;

import javax.persistence.EntityManager;
import java.util.List;

public interface GenericDAO<T, ID> {

    void create(T t);

    T findByID(ID id);

    List<T> findAll();

    void update(T t);

    void delete(T t);

}
