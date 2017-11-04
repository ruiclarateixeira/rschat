package com.ruiteixeira.chatapp.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Online user management
 */
class UserManager {
    private static Map<String, User> users = new ConcurrentHashMap<String, User>();

    static User getUser(String username) {
        return users.get(username);
    }

    static void addUser(User user) {
        users.put(user.getName(), user);
    }
}
