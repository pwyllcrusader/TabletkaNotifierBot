package by.pw.crr.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Medicine {
    @Id
    private Long medicineId;
    @NotNull
    private String medicineName;

    public Medicine() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        if (!medicineId.equals(medicine.medicineId)) return false;
        return medicineName.equals(medicine.medicineName);
    }

    @Override
    public int hashCode() {
        int result = medicineId.hashCode();
        result = 31 * result + medicineName.hashCode();
        return result;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
}
