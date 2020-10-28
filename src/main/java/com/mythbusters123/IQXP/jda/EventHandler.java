package com.mythbusters123.IQXP.jda;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mythbusters123.IQXP.util.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.*;

public class EventHandler extends ListenerAdapter {

    Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);
    private int number = 1;
    private String message;
    String sortedInactive = sortFile(this.filepath + "/inactivechannels.txt");
    String sortedStaff = sortFile(this.filepath + "/staffchannels.txt");
    @Override
    //where all the magic happens, except it doesnt and sometimes doesnt work ¯\_(ツ)_/¯
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        this.message = "";
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(sortedInactive.contains(message.getId())) {
            //sets inactive, i think
            setInactive(content, message, event, message.getMentionedMembers(), Objects.requireNonNull(message.getMember()));
        } else if(sortedStaff.contains(message.getId())) {
            showInactive(content, message, event);
            checkXP(content, message, event);
        }


    }
    String playerInfo = "https://api.slothpixel.me/api/players/";
    String filepath = Paths.get(".").toAbsolutePath().normalize().toString() + "/inactivetryhards.txt";
    //if you dont get this you are too stupid for it to be explained to you
    public void buildEmbed(String description, MessageChannel channel, String title) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(new Color(0, 255, 55));
        eb.setDescription(description);
        channel.sendMessage(eb.build()).queue();
    }
    public static String sortFile(String file) {
        String src;
        Scanner sc = null;
        try {
            sc = new Scanner(new File(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder b = new StringBuilder();
        int i = 0;
        while(true) {
            assert sc != null;
            if (!sc.hasNextLine()) break;
            i++;
            b.append(i).append(". ").append(sc.nextLine());
        }
        src = b.toString();
        return src;
    }

    public JsonParser jp = new JsonParser();
    public void showInactive(String content, Message message, GuildMessageReceivedEvent event){
        MessageChannel channel = event.getChannel();

        if(!content.startsWith("!showinactive")) return;
        else {
                LOGGER.debug("Gathering Players...");

                buildEmbed(sortFile(this.filepath), channel, "List of inactive players:");
        }
    }

    Map<String, Double> players = new LinkedHashMap<>();
    Map<String, Double> listOfPlayers = new LinkedHashMap<>();

    public void checkXP(String content, Message message, GuildMessageReceivedEvent event) {

        this.message = "";
        this.number = 1;
        if(message.getAuthor().isBot()) {return;}

        if(!content.startsWith("!checkxp")) {return;} else {
            LOGGER.debug("Checking XP");
            MessageChannel channel = event.getChannel();

            JsonParser jsonParser = new JsonParser();

            //does something
            jsonParser.parseData();
            int sizeOfMembers = JsonParser.members.size();


            for(int i = 0; i < sizeOfMembers; i++) {
                JsonParser.Member memberIndex = JsonParser.members.get(i);
                if(this.message.length() > 1000 || i == sizeOfMembers - 1) {
                    LOGGER.debug("Sorting Map");
                    this.listOfPlayers = sortMap(players);
                    LOGGER.debug("constructing message");
                    constructMessage();
                    LOGGER.debug("Building Embed");
                    buildEmbed(this.message, channel, "List of Players to kick:");
                } else
                if(Instant.now().toEpochMilli() > (memberIndex.getJoined()+7*24*60*60*1000) && !sortFile(this.filepath).contains(memberIndex.getName(memberIndex.getUuid()))) {
                    switch(memberIndex.getRank()){
                        case "Staff":
                            if(memberIndex.getSum() < 10000) {
                                JsonObject lastjoined = new Gson().fromJson(jp.getPlayerData(playerInfo + memberIndex.getName(memberIndex.getUuid())), JsonObject.class);
                                String ldt = Instant.ofEpochMilli(lastjoined.get("last_joined").getAsLong()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
                                LOGGER.debug(memberIndex.getName(memberIndex.getUuid()) + " failed XP Test with " + memberIndex.getSum() + " GEXP. " + "Their rank is Staff");
                                players.put(memberIndex.getName(memberIndex.getUuid()) + " ("  + memberIndex.getRank() + ")" + "Last Joined - "  + ldt, memberIndex.getSum());
                            }
                            break;
                        case "1st Quad":
                            if(memberIndex.getSum() < 75000) {
                                JsonObject lastjoined = new Gson().fromJson(jp.getPlayerData(playerInfo + memberIndex.getName(memberIndex.getUuid())), JsonObject.class);
                                String ldt = Instant.ofEpochMilli(lastjoined.get("last_joined").getAsLong()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
                                LOGGER.debug(memberIndex.getName(memberIndex.getUuid()) + " failed XP Test with " + memberIndex.getSum() + " GEXP. " + "Their rank is Staff");
                                players.put(memberIndex.getName(memberIndex.getUuid()) + " ("  + memberIndex.getRank() + ")" + "Last Joined - "  + ldt, memberIndex.getSum());
                            }
                            break;
                        //wth does this do, idk intellij told me to
                        case "2nd Quad":
                        case "Senior":
                            if(memberIndex.getSum() < 50000) {
                                JsonObject lastjoined = new Gson().fromJson(jp.getPlayerData(playerInfo + memberIndex.getName(memberIndex.getUuid())), JsonObject.class);
                                String ldt = Instant.ofEpochMilli(lastjoined.get("last_joined").getAsLong()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();
                                LOGGER.debug(memberIndex.getName(memberIndex.getUuid()) + " failed XP Test with " + memberIndex.getSum() + " GEXP. " + "Their rank is Staff");
                                players.put(memberIndex.getName(memberIndex.getUuid()) + " ("  + memberIndex.getRank() + ")" + "Last Joined - "  + ldt, memberIndex.getSum());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            this.number = 1;
        }
    }

    String kickMessage;
    //sets inactive or active
    //WIP, i think it works but idk lol, copied off from some rando website, idk if it works but ok
    public void setInactive(String content, Message message, GuildMessageReceivedEvent event, java.util.List<Member> members, Member author) {
        this.kickMessage = "";
        if(!author.getRoles().contains(author.getGuild().getRoleById(480358874839252992L))) return;
        if(message.getAuthor().isBot()) {return;}
        MessageChannel mc = event.getChannel();
        if(content.startsWith("!setinactive")) {
            if(members.isEmpty()) {
                mc.sendMessage(author.getAsMention() + " You must declare a user to setinactive!").queue();
            } else {
                System.out.println("Setting player inactive");
                File file = new File(this.filepath);
                LOGGER.debug("checking if file exists");
                if (!file.exists()) {
                    LOGGER.debug("File does not exist");
                    try {
                        boolean completed = file.createNewFile();
                        if(completed) {
                            LOGGER.debug("Created File inactivetryhards.txt");
                        } else{
                            LOGGER.error("Was not able to create new file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try{
                    String nickname;

                    LOGGER.debug("File exists, moving on with writing to it...");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true));
                    for(int i = 0; i < members.size(); i++) {
                        if(message.getMentionedMembers().get(i).getNickname() != null) {
                            nickname = message.getMentionedMembers().get(i).getNickname();
                        } else {
                            nickname = message.getMentionedMembers().get(i).getEffectiveName();
                        }
                        LOGGER.debug("Appending File with " + nickname);
                        bw.write(nickname + "\n");
                        if (i < members.size() - 1) {
                            this.kickMessage = this.kickMessage.concat(nickname + "\n");
                            LOGGER.debug(members.get(i).getNickname());
                        } else if (i == members.size() - 1) {
                            this.kickMessage = this.kickMessage.concat(nickname + "");
                        }
                    }
                    bw.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                mc.sendMessage(author.getAsMention() + " set " + this.kickMessage + " inactive").queue();
            }
        }
        else if(content.startsWith("!removeinactive")) {
            if(members.isEmpty()) {
                mc.sendMessage(author.getAsMention() + " You must declare a user (mentioning them) to make them active!").queue();
            }
            else {
                try {
                    LOGGER.debug("Initializing Variables for Removal");
                    Scanner sc = new Scanner(new File(this.filepath));
                    StringBuilder buffer = new StringBuilder();
                    LOGGER.debug("grabbing data from file");
                    while(sc.hasNextLine()) {
                        buffer.append(sc.nextLine()).append(System.lineSeparator());
                    }
                    String fileContents = buffer.toString();
                    sc.close();
                    for (Member member : members) {
                        fileContents = fileContents.replaceAll(Objects.requireNonNull(member.getNickname()), "");
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, false));
                    bw.write(fileContents);
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.debug("Removal Failed with exception" + e.toString());
                }
                LOGGER.debug("Player Removed Successfully");
                mc.sendMessage(author.getAsMention() + " set " + members.get(0).getAsMention() + " active").queue();
            }
        }
    }

    //No idea what this does I copy-pasted it off stackoverflow
    //sorts the map
    public <K, V> Map<K, V> sortMap(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (Comparator<Object>) (o1, o2) -> ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    //constructs the message
    public void constructMessage() {
        for(Map.Entry<String, Double> entry : listOfPlayers.entrySet()) {
            this.message = this.message.concat(this.number + ". " + entry.getKey() + " - " + entry.getValue().toString().replace(".0", "") + " GEXP \n");
            this.number++;
        }
    }
}
