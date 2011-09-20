package bots;

import api.MessageHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.ChatRoom;
import models.Message;
import models.User;

public enum ImagesBot implements MessageHandler {

    INSTANCE {

        private static final String URL_REGEX = "^(?:(?![^:@]+:[^:@/]*@)([^:/?#.]+):)?(?://)?((?:(([^:@]*):?([^:@]*))?@)?([^:/?#]*)(?::(\\d*))?)(((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[?#]|$)))*/?)?([^?#/]*))(?:\\?([^#]*))?(?:#(.*))?)";

        public Message handleChatMessage(Message message, User user, ChatRoom room) {
            Pattern p = Pattern.compile(URL_REGEX);
            String text = message.text;
            Matcher m = p.matcher(text);
            while(m.find()) {
                String url = m.group();
                String html = "<a href=\"" + url + "\">" + url + "</a>";
                text = text.replace(url, html);
                if (url.endsWith(".jpg") || url.endsWith(".png")) {
                    text += "<br/><br/><img src=\"" + url + "\" >";
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
