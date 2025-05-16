package objects;

import java.time.LocalDate;

public class Prescription {

    @SuppressWarnings("unused")
    private int id;
    private String name;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;

    public Prescription(int id, String name, String dosage, String frequency, String route, LocalDate startDate,
            LocalDate endDate) {

        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s) from %s to %s.", name, dosage, frequency, startDate, endDate);
    }
}
