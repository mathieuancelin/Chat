package bots;

import api.MessageHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.ChatRoom;
import models.Message;
import models.User;

public enum GistBot implements MessageHandler {

    INSTANCE {

        private static final String GIST_REGEX = "https://gist.github.com/[0-9]+";
        
        public Message handleChatMessage(Message message, User user, ChatRoom room) {
            Pattern p = Pattern.compile(GIST_REGEX);
            String text = message.text;
            Matcher m = p.matcher(text);
            while(m.find()) {
                String url = m.group();
                    text += "<br/><script src=\"" + url + ".js\"> </script>";
            }
            message.text = text;
            return message.save();
        }

        public void registerChatRoom(String room) {
            //nothing
        }
    }
}
