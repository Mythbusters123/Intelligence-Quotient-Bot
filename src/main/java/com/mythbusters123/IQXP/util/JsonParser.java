package com.mythbusters123.IQXP.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JsonParser {
    public Logger LOGGER = LoggerFactory.getLogger(JsonParser.class);
    DataUtil du = new DataUtil();
     public static ArrayList<Member> members = new ArrayList<>();
    JsonObject jo = new Gson().fromJson(du.getData("https://api.slothpixel.me/api/guilds/Prithibi"), JsonObject.class);

    //Adding this because its throwing errors otherwise
    public String getPlayerData(String url) {
        return du.getData(url);
    }

    public void parseData() {
        LOGGER.debug("Parsing data");
        jo.getAsJsonArray("members").forEach(member -> members.add(new Member(member.getAsJsonObject())));

    }
    public static class Member{
        Logger LOGGER = LoggerFactory.getLogger(JsonParser.Member.class);
        private final String uuid;
        private final double sum;
        private final long joined;
        private final String rank;
        private long lastJoined;
        Member(JsonObject data) {
            uuid = data.get("uuid").toString();
            joined = data.get("joined").getAsLong();
            rank = data.get("rank").getAsString();
            LOGGER.debug("Getting EXP for " + getName(getUuid()));
            LocalDate ld = LocalDate.now();
            ArrayList<Integer> expRawValues = new ArrayList<>();
            for(int i = 0; i < 7; i++) {
                expRawValues.add(data.getAsJsonObject("exp_history").get(ld.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getAsInt());
            }
            sum = expRawValues.stream().mapToDouble(a -> a).sum();
        }
        public double getSum()  { return sum;       }
        public long getJoined() { return joined;    }
        public String getUuid() { return uuid;      }
        public String getRank() { return rank;      }


        public String getName(String uuid) {

            String name;
            DataUtil du = new DataUtil();
            uuid = uuid.replace("\"", "");
            JsonObject jo = new Gson().fromJson(du.getData("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid), JsonObject.class);
            name = jo.get("name").toString().replace("\"", "");
            return name;
        }
    }

}
