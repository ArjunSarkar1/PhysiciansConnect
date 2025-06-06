package physicianconnect.objects;

public class Receptionist {
    private final String id;
    private String name;
    private final String email;
    private final String password; // hashed in production

    public Receptionist(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return "receptionist";
    }

    public void setName(String name) {
        this.name = name;
    }
}