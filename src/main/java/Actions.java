import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Actions extends ListenerAdapter {

    public static void ASendMessage(String message, MessageReceivedEvent event){

        if(message.contains("$user")){
           message = message.replace("$user", event.getAuthor().getAsMention());
        }
        event.getChannel().asTextChannel().sendMessage(message).queue();

    }

    public static void ACreateTChannel(String name, MessageReceivedEvent event){

        event.getGuild().createTextChannel(name).queue();

    }
}
