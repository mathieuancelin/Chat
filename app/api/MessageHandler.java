package api;

import models.ChatRoom;
import models.Message;
import models.User;

public interface MessageHandler {

    void registerChatRoom(String room);

    Message handleChatMessage(Message message, User user, ChatRoom room);

}
