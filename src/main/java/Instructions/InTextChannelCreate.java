package Instructions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class InTextChannelCreate {
    private final JDA jda;
    private final Guild guild;
    private String name;

    public InTextChannelCreate(JDA jda, Guild guild, String name) {
        this.jda = jda;
        this.guild = guild;
        this.name = name;
        createChannel();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        // update the channel name if it has already been created
        TextChannel channel = guild.getTextChannelsByName(name, true).stream().findFirst().orElse(null);
        if (channel != null) {
            channel.getManager().setName(name).queue();
        }
    }

    private void createChannel() {
        guild.createTextChannel(name).queue();
    }

    public static InTextChannelCreate fromJson(JsonObject jsonObject, JDA jda, Guild guild) throws JsonParseException {
        JsonObject createChannelObject = jsonObject.getAsJsonObject("CREATE TEXT CHANNEL");

        if (createChannelObject == null) {
            throw new JsonParseException("Invalid JSON: missing 'CREATE CHANNEL' field");
        }

        String name = createChannelObject.get("name").getAsString();
        return new InTextChannelCreate(jda, guild, name);
    }

    @Override
    public String toString() {
        return "InTextChannelCreate{" +
                "name='" + name + '\'' +
                '}';
    }
}
