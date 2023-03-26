package org.example.menu;

import org.example.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Card {
    Long card_id;
    String dish_name;
    String card_descr;
    Double card_time;
    Long equip_type;
    Map<Long, Operations> oper;

    public Card(JSONObject stuff) {
        oper = new HashMap<>();
        this.card_id = (Long) stuff.get("card_id");
        this.dish_name = (String) stuff.get("dish_name");
        this.card_descr = (String) stuff.get("card_descr");
        this.card_time = Double.valueOf(stuff.get("card_time").toString());
        this.equip_type = (Long) stuff.get("equip_type");
        for (Object item : (JSONArray) stuff.get("operations")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("oper_type")) {
                Long id = (Long) ((JSONObject) item).get("oper_type");
                oper.put(id, new Operations((JSONObject) item));
            }
        }
    }
}

