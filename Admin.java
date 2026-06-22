package models;

public class Admin extends User {
    public Admin(String id, String name, String password) {
        super(id, name, password, "Admin");
    }

    @Override
    public String toCsvRow() {
        return id + "," + name + "," + password + "," + role + ",N/A,N/A";
    }
}