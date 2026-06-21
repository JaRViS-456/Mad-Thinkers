package models;

public abstract class User {
    protected String id;
    protected String name;
    protected String password;
    protected String role;

    public User(String id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public abstract String toCsvRow();
}