package by.pw.crr.dao;

import by.pw.crr.entities.ChatUser;

import javax.persistence.EntityManager;

public class ChatUserDAO extends GenericDAOImpl<ChatUser, Long> {

    public ChatUserDAO() {
        super(ChatUser.class);
    }
}
