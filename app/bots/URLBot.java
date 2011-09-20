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

        public Message handleChatMessage(Message message, User user, ChatRoom room) {
            Pattern p = Pattern.compile(URL_REGEX);
            String text = message.text;
            Matcher m = p.matcher(text);
            while(m.find()) {
                String url = m.group();
                String html = "<a href=\"" + url + "\">" + url + "</a>";
                text = text.replace(url, html);
            }
            message.text = text;
            return message.save();
        }

        public void registerChatRoom(String room) {
            //nothing
        }
    }
}
