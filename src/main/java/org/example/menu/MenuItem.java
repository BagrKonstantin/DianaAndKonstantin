package org.example.menu;

import org.example.storage.Product;
import org.example.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;

public class MenuItem implements Serializable {
    Long menu_dish_id;
    Long menu_dish_card;
    Long menu_dish_price;
    Boolean menu_dish_active;

    public MenuItem(JSONObject stuff) throws IOException, ParseException {
        this.menu_dish_id = (Long) stuff.get("menu_dish_id");
        this.menu_dish_card = (Long) stuff.get("menu_dish_card");
        this.menu_dish_price = (Long) stuff.get("menu_dish_price");
        this.menu_dish_active = (Boolean) stuff.get("menu_dish_active");
    }

    public Long getMenu_dish_card() {
        return menu_dish_card;
    }
}

