package by.pw.crr.dao;

import by.pw.crr.entities.Medicine;

import javax.persistence.EntityManager;

public class MedicineDAO extends GenericDAOImpl<Medicine, Long> {

    public MedicineDAO() {
        super(Medicine.class);
    }
}
