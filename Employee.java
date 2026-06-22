package models;

public class Employee extends User {
    private String shift;
    private String phone;

    public Employee(String id, String name, String password, String shift, String phone) {
        super(id, name, password, "Employee");
        this.shift = shift;
        this.phone = phone;
    }

    public String getShift() { return shift; }
    public String getPhone() { return phone; }
    public void setShift(String shift) { this.shift = shift; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toCsvRow() {
        return id + "," + name + "," + password + "," + role + "," + shift + "," + phone;
    }

    public static Employee fromCsvRow(String[] data) {
        return new Employee(data[0], data[1], data[2], data[4], data[5]);
    }
}