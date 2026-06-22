package controllers;

import models.*;
import dao.CSVHandler;
import java.util.ArrayList;

public class AuthController {
    private ArrayList<User> users;
    private ArrayList<Item> items;
    private User currentUser;

    public AuthController() {
        this.users = CSVHandler.loadUsers();
        this.items = CSVHandler.loadItems();

        // Load bill counter
        int lastBill = CSVHandler.loadMaxBillCounter();
        Bill.setCounter(lastBill + 1);
    }

    public User authenticate(String id, String password, boolean isAdmin) {
        User user = findUserById(id);

        if (user != null && user.authenticate(password)) {
            // Check role matches login type
            if (isAdmin && user instanceof Admin) {
                currentUser = user;
                return user;
            } else if (!isAdmin && user instanceof Employee) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public User findUserById(String id) {
        for (User u : users) {
            if (u.getId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public void logout() {
        currentUser = null;
    }
}