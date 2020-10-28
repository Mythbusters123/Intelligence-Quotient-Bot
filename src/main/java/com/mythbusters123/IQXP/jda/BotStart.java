package com.mythbusters123.IQXP.jda;

import com.mythbusters123.IQXP.util.JsonParser;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.List;

//This is a comment
//Starts the bot
public class BotStart extends ListenerAdapter {
    //this is the jda
    private static JDA jda;
    private static final Logger LOGGER = LoggerFactory.getLogger(BotStart.class);

    //well, i guess THIS is where the magic happens, but no one cares lol
    public static void main(String[] args) {
        try {
            //makes the jda
            LOGGER.debug("Creating bot profile....");
            jda = JDABuilder.createDefault("im not going to reveal the token").addEventListeners(new EventHandler()).build();
            jda.addEventListener(new BotStart());
            Scanner sc = new Scanner(System.in);
            while (true) {
                if (sc.nextLine().contains("stop")) {
                    LOGGER.debug("Stopping System with exit code 0");
                    System.exit(0);
                }
            }
        } catch (LoginException e) {
            //catches exception
            e.printStackTrace();
        }
    }
    @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        MessageChannel mc = message.getChannel();
        String content = message.getContentRaw();
        if(message.getAuthor().isBot()) return;
        if(content.startsWith("!setstaffchannel ")) {
            if(content.replace("!setstaffchannel ", "").equals("")) {
                mc.sendMessage(event.getAuthor().getAsMention() + " You must input the message ID").queue();
            } else {
                File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/staffchannels.txt");
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
                  bufferedWriter.write(content.replace("!setstaffchannel ", ""));
                  bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(content.startsWith("!setinactivechannel")) {
            if(content.replace("!setinactivechannel", "").equals("")) {
                mc.sendMessage(event.getAuthor().getAsMention() + " You must input the message ID").queue();
            } else {
                File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "inactivechannels.txt");
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                  bufferedWriter.write(content.replace("!setinactivechannel", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
