import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import RssReader.RssMain;

import java.io.*;
import java.util.logging.Logger;

public class SimpleBot extends TelegramLongPollingBot {

   // private final static Logger _Log = Logger.getLogger(SimpleBot.class.getName());

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new SimpleBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "IReader";
    }

    @Override
    public String getBotToken() {
        return "419843093:AAF3YZXxZIWVVh6Sh8klXmyijxekFAl0UMg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (message.getText().equals("/help") || message.getText().equals("/start")) {
                try (BufferedReader myfile =
                             new BufferedReader(new FileReader("help.txt"))) {
                    String str;
                    StringBuilder sb=new StringBuilder();
                    while((str=myfile.readLine())!=null) {
                        sb.append(str+"\n");
                    }
                    sendMsg(message, sb.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(message.getText().equals("/movies") || message.getText().equals("/politics") ||
                    message.getText().equals("/music") || message.getText().equals("/games") ||
                    message.getText().equals("/health") || message.getText().equals("/science") ||
                    message.getText().equals("/football") || message.getText().equals("/showbusiness") ||
                    message.getText().equals("/business") || message.getText().equals("/cosmos")
                    ){
                String URL_str = new String("https://news.yandex.ru"+message.getText()+".rss");

                sendMsg(message, URL_str);
                RssMain feed = new RssMain();
                feed.read(URL_str);
                //feed.writeToFile();
                //feed.writeToRssFile();
                String[] sb=feed.textNews();
                for(int i=0;i<sb.length;i++) {
                    if(sb[i]!=null)
                        sendMsg(message, sb[i]);
                }
            }
            else
                sendMsg(message, "Enter /help to get more information");
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}