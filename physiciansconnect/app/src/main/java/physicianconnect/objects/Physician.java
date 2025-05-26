package physicianconnect.objects;

public class Physician {
    private final String id;
    private final String name;
    private final String email;
    private final String password;

    public Physician(String id, String name, String email, String password) {
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

    @Override
    public String toString() {
        return name + " (ID: " + id + ", Email: " + email + ")";
    }
}
