
import Commands.PingPong;
import com.theokanning.openai.service.OpenAiService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BotInit {

    public static void main(String[] args) throws InterruptedException {


        String token = System.getenv("Discord");

        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("your commands."))
                .addEventListeners(new PingPong(), new ChatGPT())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true)
                .build().awaitReady();

//s
        jda.upsertCommand("ping","pong").queue();

    }

}
