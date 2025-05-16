package objects;

public class MedicalHistory {

    private String pastConditions;
    private String surgeries;
    private String allergies;
    private String immunizations;
    private String hospitalizations;
    private String familyHistory;

    public MedicalHistory(
            String pastConditions,
            String surgeries,
            String allergies,
            String immunizations,
            String hospitalizations,
            String familyHistory) {

        this.pastConditions = pastConditions;
        this.surgeries = surgeries;
        this.allergies = allergies;
        this.immunizations = immunizations;
        this.hospitalizations = hospitalizations;
        this.familyHistory = familyHistory;
    }

    // Getters and Setters
    public String getPastConditions() {
        return pastConditions;
    }

    public void setPastConditions(String pastConditions) {
        this.pastConditions = pastConditions;
    }

    public String getSurgeries() {
        return surgeries;
    }

    public void setSurgeries(String surgeries) {
        this.surgeries = surgeries;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getImmunizations() {
        return immunizations;
    }

    public void setImmunizations(String immunizations) {
        this.immunizations = immunizations;
    }

    public String getHospitalizations() {
        return hospitalizations;
    }

    public void setHospitalizations(String hospitalizations) {
        this.hospitalizations = hospitalizations;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }
}
