package objects;

import java.util.List;

public class Physician extends User {

    private int officeId;

    Physician(int id, String firstName, String lastName, String email, int officeId) {

        super(id, firstName, lastName, email);
        this.officeId = officeId;
    }

    // Optionally include a constructor that accepts appointments
    Physician(int id, String firstName, String lastName, String email, int officeId,
            List<Appointment> appointmentsList) {

        super(id, firstName, lastName, email);
        this.officeId = officeId;
    }

    // Getters and Setters
    public int getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }
}
