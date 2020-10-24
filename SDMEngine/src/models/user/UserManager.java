package models.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserManager {

    private final Set<User> userSet;
    private final Set<String> userNameSet;

    public UserManager() {
        userNameSet = new HashSet<>();
        userSet = new HashSet<>();
    }

    public synchronized void addUser(String username) {
        userNameSet.add(username);
    }

    public synchronized void removeUser(String username) {
        userNameSet.remove(username);
    }

    public synchronized Set<String> getUserNames() {
        return Collections.unmodifiableSet(userNameSet);
    }

    public boolean isUserExists(String username) {
        Set<String> users = userSet.stream().map(user -> user.getName()).collect(Collectors.toSet());
        boolean isUserExists = users.contains(username);
        System.out.println("Does user with name " + username + "already exist?: " + isUserExists);
        return isUserExists;
    }

    public synchronized void addUser(String username, String role) {
        if (role.equals("customer")){
            userSet.add(new Customer(username,role));
            System.out.println("UserManager created new Customer with name " + username);
        } else if (role.equals("owner")){
            userSet.add(new Owner(username,role));
            System.out.println("UserManager created new Owner with name " + username);
        }
        userNameSet.add(username);
    }

    public User getUserByName(String username){
        for (User user: userSet){
            if (user.getName().equals(username)){
                return user;
            }
        }
        return null;
    }

    public HashMap<String, String> getOnlineUsers() {
        HashMap<String,String> res = new HashMap<>();
        userNameSet.forEach(username->{
            User onlineUser = getUserByName(username);
            res.put(onlineUser.getName(),onlineUser.getRole());
        });
        return res;
    }
}
