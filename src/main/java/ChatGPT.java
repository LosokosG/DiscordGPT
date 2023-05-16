import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatGPT extends ListenerAdapter {

    String prompt;


    @Override
    public void onReady(ReadyEvent event) {
        try {
            prompt = new String(Files.readAllBytes(Paths.get("src\\main\\java\\prompt.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User botUser = event.getJDA().getSelfUser();
        Message message = event.getMessage();

        if(message.getMentions().getUsers().contains(botUser) && !message.getAuthor().isBot()){

            String messageContent = message.getContentRaw();

            JDA jda = event.getJDA();
            Guild guild = event.getGuild();
            TextChannel currentTextChannel = event.getChannel().asTextChannel();


            String output = GPTcall(messageContent);

            executeActions(output, event);

            System.out.println(output);

        }
    }


    private String GPTcall(String input){
        OpenAiService service = new OpenAiService(System.getenv("OpenAI"));

        String output;

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage chatResponseMessage = new ChatMessage();
        chatResponseMessage.setRole("system");
        chatResponseMessage.setContent(prompt);

        messages.add(chatResponseMessage);

        ChatMessage userInputMessage = new ChatMessage();
        userInputMessage.setRole("user");
        userInputMessage.setContent(input + "( REMEMBER TO ONLY RESPOND WITH COMMANDS!!! )");

        messages.add(userInputMessage);


        ChatCompletionRequest request = ChatCompletionRequest.builder()
        .model("gpt-3.5-turbo")
        .temperature(0.2)
        .messages(messages)
        .maxTokens(280)
        .build();

        System.out.println(request.toString());


        output = service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();

        return output;
    }

    public void executeActions(String output, MessageReceivedEvent event){


        Scanner scanner = new Scanner(output);
        String line;
        boolean isValid = false;

        while(scanner.hasNext()){
            line = scanner.nextLine();

            if(line.startsWith("SEND_MESSAGE")){
                Actions.ASendMessage( line.replace("SEND_MESSAGE", ""), event);
                isValid = true;
            }

            if(line.startsWith("CREATE_T_CHANNEL")){
                Actions.ACreateTChannel( line.replace("CREATE_T_CHANNEL", ""), event);
                isValid = true;
            }

        }

        if(!isValid)
            event.getChannel().asTextChannel().sendMessage("The result was not a valid command. Here is the AI's reponse:\n" + output).queue();

        scanner.close();
    }
}
