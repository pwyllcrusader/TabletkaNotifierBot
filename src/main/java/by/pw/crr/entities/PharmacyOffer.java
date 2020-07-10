package by.pw.crr.entities;

import com.google.common.base.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PharmacyOffer {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String pharmacy;
    @NotNull
    private String pharmacyAddress;
    @ManyToOne
    @JoinColumn(name = "medicine_medicineId", nullable = false)
    private Medicine medicine;
    private String cost;
    @ManyToOne
    @JoinColumn(name = "chatUser_chatId", nullable = false)
    private ChatUser chatUser;
    @NotNull
    private boolean isSent;

    public PharmacyOffer() {
    }

    public PharmacyOffer(String pharmacy, String pharmacyAddress, Medicine medicine, String cost, ChatUser chatUser) {
        this.pharmacy = pharmacy;
        this.pharmacyAddress = pharmacyAddress;
        this.medicine = medicine;
        this.cost = cost;
        this.chatUser = chatUser;
        this.isSent = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PharmacyOffer that = (PharmacyOffer) o;
        return Objects.equal(pharmacy, that.pharmacy) &&
                Objects.equal(pharmacyAddress, that.pharmacyAddress) &&
                Objects.equal(medicine, that.medicine) &&
                Objects.equal(cost, that.cost) &&
                Objects.equal(chatUser, that.chatUser);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pharmacy, pharmacyAddress, medicine, cost, chatUser);
    }

    @Override
    public String toString() {
        return "*" + medicine.getMedicineName() + "*" + "\n" +
                "====================" + "\n" + "   " + "\n" +
                "*Аптека:* " + pharmacy + "\n" +
                "*Адрес:* " + pharmacyAddress + "\n" +
                "*Стоимость:* " + cost + "\n" +
                "[Ссылка](https://tabletka.by/result?ls=" + medicine.getMedicineId() + "&region=" + chatUser.getLocationId()+")";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(String pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public void setChatUser(ChatUser chatUser) {
        this.chatUser = chatUser;
    }
}
