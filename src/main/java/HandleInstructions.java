import Instructions.InSendMessage;
import Instructions.InTextChannelCreate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.theokanning.openai.service.OpenAiService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HandleInstructions {

    public static void handleInstructions(JsonObject instruction, MessageReceivedEvent event) {
        OpenAiService service = new OpenAiService(System.getenv("OpenAI"));

        JDA jda = event.getJDA();
        Guild guild = event.getGuild();
        TextChannel currentTextChannel = event.getGuildChannel().asTextChannel();
        Message userMessage = event.getMessage();
        User currentUser = event.getAuthor();

        try {
            InSendMessage inSendMessage = InSendMessage.fromJson(instruction, jda, currentTextChannel);

            return;
        } catch (JsonParseException e) {
            System.out.println("Failed to parse InSendMessage from JSON: " + e.getMessage());
        }

        try {
            InTextChannelCreate inTextChannelCreate = InTextChannelCreate.fromJson(instruction, jda, guild);

           return;
        } catch (JsonParseException e) {
            System.out.println("Failed to parse InTextChannelCreate from JSON: " + e.getMessage());

        }



    }


}
