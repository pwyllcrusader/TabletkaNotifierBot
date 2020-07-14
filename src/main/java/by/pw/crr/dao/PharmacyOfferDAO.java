package by.pw.crr.dao;

import by.pw.crr.entities.PharmacyOffer;

import javax.persistence.EntityManager;

public class PharmacyOfferDAO extends GenericDAOImpl<PharmacyOffer,Long> {

    public PharmacyOfferDAO() {
        super(PharmacyOffer.class);
    }
}
