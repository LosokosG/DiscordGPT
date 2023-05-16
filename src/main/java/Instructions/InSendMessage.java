package Instructions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InSendMessage extends ListenerAdapter {


        private JDA jda;
        private TextChannel channel;
        private String message;

        public InSendMessage(JDA jda, TextChannel channel, String message) {
            this.jda = jda;
            this.channel = channel;
            this.message = message;

            // Send the message using JDA
            this.channel.sendMessage(this.message).queue();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "InSendMessage{" +
                    "message='" + message + '\'' +
                    '}';
        }

    public static InSendMessage fromJson(JsonObject jsonObject, JDA jda, TextChannel channel) throws JsonParseException {
        JsonObject sendMessageObject = jsonObject.getAsJsonObject("SEND MESSAGE");

        if (sendMessageObject == null) {
            throw new JsonParseException("Invalid JSON: missing 'SEND MESSAGE' field");
        }

        String message = sendMessageObject.get("message").getAsString();
        return new InSendMessage(jda, channel, message);
    }

}

