package controllers;

import models.*;
import dao.CSVHandler;
import java.util.ArrayList;

public class UserController {
    private ArrayList<User> users;

    public UserController(ArrayList<User> users) {
        this.users = users;
    }

    public boolean addEmployee(String id, String name, String password, String shift, String phone) {
        // Validation
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            return false;
        }

        // Check for duplicate ID
        for (User u : users) {
            if (u.getId().equalsIgnoreCase(id.trim())) {
                return false;
            }
        }

        Employee employee = new Employee(
                id.trim(),
                name.trim(),
                password != null ? password : "default123",
                shift != null && !shift.trim().isEmpty() ? shift.trim() : "Day",
                phone != null ? phone.trim() : ""
        );

        users.add(employee);
        CSVHandler.saveUsers(users);
        return true;
    }

    public boolean updateEmployee(String id, String name, String password, String shift, String phone) {
        Employee employee = findEmployeeById(id);
        if (employee == null) return false;

        if (name != null && !name.trim().isEmpty()) {
            employee.setName(name.trim());
        }
        if (password != null && !password.trim().isEmpty()) {
            employee.setPassword(password.trim());
        }
        if (shift != null && !shift.trim().isEmpty()) {
            employee.setShift(shift.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            employee.setPhone(phone.trim());
        }

        CSVHandler.saveUsers(users);
        return true;
    }

    public boolean deleteEmployee(String id) {
        Employee employee = findEmployeeById(id);
        if (employee == null) return false;

        users.remove(employee);
        CSVHandler.saveUsers(users);
        return true;
    }

    public Employee findEmployeeById(String id) {
        for (User u : users) {
            if (u instanceof Employee && u.getId().equalsIgnoreCase(id)) {
                return (Employee) u;
            }
        }
        return null;
    }

    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Employee) {
                employees.add((Employee) u);
            }
        }
        return employees;
    }
}