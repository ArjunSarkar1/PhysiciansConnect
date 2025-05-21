package physicianconnect.objects;

import java.time.LocalDate;

public class Prescription {

    private int id;
    private String name;
    private String dosage;
    private String frequency;
    private String doctorsNote;
    private LocalDate startDate;
    private LocalDate endDate;

    public Prescription(int id, String name, String dosage, String frequency, String doctorsNote, LocalDate startDate,
            LocalDate endDate) {

        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.doctorsNote = doctorsNote;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getPrescriptionId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return this.dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDoctorsNote() {
        return this.doctorsNote;
    }

    public void setDoctorsNote(String doctorsNote) {
        this.doctorsNote = doctorsNote;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
