import Instructions.InSendMessage;
import com.google.gson.*;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChatGPT extends ListenerAdapter {

    String prompt;


    @Override
    public void onReady(ReadyEvent event) {
        try {
            prompt = new String(Files.readAllBytes(Paths.get("src\\main\\java\\prompt.txt")));
            System.out.println(prompt);
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


            String input = GPTcall(messageContent);

            System.out.println(input);

            Object json = parseJson(input);

            try {
                if (json instanceof JsonArray jsonArray) {
                    // Handle the JSON array
                    System.out.println("Parsed JSON Array: " + jsonArray.toString());

                    for (JsonElement jsonElement : jsonArray) {
                        if (jsonElement instanceof JsonObject jsonObject) {
                            HandleInstructions.handleInstructions(jsonObject, event);
                        }
                    }

                } else if (json instanceof JsonObject jsonObject) {
                    // Handle the JSON object
                    System.out.println("Parsed JSON Object: " + jsonObject.toString());

                    HandleInstructions.handleInstructions(jsonObject, event);


                } else {
                    // Handle the case where the returned object is not a JSON array or object
                    JsonObject responseJson = new JsonObject();
                    JsonObject sendMessageObject = new JsonObject();
                    sendMessageObject.addProperty("message", String.valueOf(input));
                    responseJson.add("SEND MESSAGE", sendMessageObject);
                    InSendMessage inSendMessage = InSendMessage.fromJson(responseJson, jda, currentTextChannel);


                }
            } catch (Exception e) {
                System.out.println("Error handling JSON: " + e.getMessage());
            }

        }
    }


    private String GPTcall(String input){
        OpenAiService service = new OpenAiService(System.getenv("OpenAI"));

        String output = "";

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage chatResponseMessage = new ChatMessage();
        chatResponseMessage.setRole("assistant");
        chatResponseMessage.setContent(prompt);

        messages.add(chatResponseMessage);

        ChatMessage userInputMessage = new ChatMessage();
        userInputMessage.setRole("user");
        userInputMessage.setContent(input);

        messages.add(userInputMessage);



        ChatCompletionRequest request = ChatCompletionRequest.builder()
        .model("gpt-3.5-turbo")
        .temperature(0.2)
        .messages(messages)
        .build();

        output = service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();

        return output;
    }


    public static Object parseJson(String input) {
        Gson gson = new Gson();
        try {
            // Try to parse the input as a JSON array
            JsonArray jsonArray = gson.fromJson(input, JsonArray.class);
            return jsonArray;
        } catch (JsonSyntaxException e) {
            // Parsing as JSON array failed, so try to parse as JSON object
            try {
                JsonObject jsonObject = gson.fromJson(input, JsonObject.class);
                return jsonObject;
            } catch (JsonSyntaxException ex) {
                // Parsing as JSON object also failed, so return null
                return null;
            }
        }
    }



}
