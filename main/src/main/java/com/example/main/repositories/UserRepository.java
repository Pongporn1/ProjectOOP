package com.example.main.repositories;

import com.example.main.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("UserRepository")
public class UserRepository {
    Set<User> userList;
    int onlineUser = 0;

    public UserRepository() {
        userList = new HashSet<>();
    }

    public void addOnlineUser() {
        onlineUser++;
    }

    public void removeOnlineUser() {
        onlineUser--;
    }

    public void removeUserBySessionId(String sessionId) {
        System.out.println(userList.stream().filter(u -> u.getSessionId().equals(sessionId)).findFirst().get().getUsername() + " Disconnected");
        userList.removeIf(user -> user.getSessionId().equals(sessionId));
    }

    public int getOnlineUser(){
        return 0;
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public boolean haveUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    public int getUserCount() {
        return userList.size();
    }

    public void removeUser(String username) {
        User toRemove = null;
        for(User user : userList) {
            if(user.getUsername().equals(username)) toRemove = user;
        }
        userList.remove(toRemove);
    }
}
