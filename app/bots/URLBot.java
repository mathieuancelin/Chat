package bots;

import api.MessageHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.ChatRoom;
import models.Message;
import models.User;

public enum URLBot implements MessageHandler {

    INSTANCE {

        private static final String R_PROTOCOL = "(http(s)?|ftp)";
        private static final String R_HOSTNAMEANDPORT = "[\\w-]+(\\.[\\w-]+)*(:(\\d+))?";
        private static final String R_RELATIVEPATH = "(/\\w+)*(/\\w+\\.\\w+)?";
        private static final String R_QUERY = "(\\?\\w+=\\w+(&(\\w+=\\w+))*)?";
        private static final String URL_REGEX = "^" + R_PROTOCOL + "://" + R_HOSTNAMEANDPORT + R_RELATIVEPATH + R_QUERY;
        private static final String IMAGE_REGEX = "(https?:((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*)";
        
        public Message handleChatMessage(Message message, User user, ChatRoom room) {
            String text = message.text;
            String[] words = message.text.split(" ");
            for (String word : words) {
                if (word.startsWith("http://") || word.startsWith("https://")) {
                    String html = "<a href=\"" + word + "\">" + word + "</a>";
                    text = text.replace(word, html);
                }
            }
            message.text = text;
            return message.save();
        }

        public void registerChatRoom(String room) {
            //nothing
        }
    }
}
