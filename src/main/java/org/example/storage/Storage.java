package org.example.storage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.example.menu.MenuAgent;
import org.example.menu.MenuItem;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;


public class Storage {
    public Map<Long, Product> storage;

    public Storage() throws IOException, ParseException {
        File file = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "input/products.txt");

        System.out.println(file.getPath());
        JSONObject o = (JSONObject) new JSONParser().parse(new FileReader(file.getPath()));

        storage = new HashMap<>();

        for (Object item : (JSONArray) o.get("products")) {
            JSONObject productObject = (JSONObject) item;
            if (productObject.containsKey("prod_item_id")) {
                long id = (Long) ((JSONObject) item).get("prod_item_id");
                storage.put(id, new Product((JSONObject) item));
                System.out.println("get");
            }
        }

    }

    public Map<Long, MenuItem> getUpdatedMenu() {
        Map<Long, MenuItem> newMenu = new HashMap<>();
        for (var item : MenuAgent.menu.values()) {
            var card = MenuAgent.cards.get(item.getMenu_dish_card());
            for(var operations : card.getOper().values()) {
                boolean flag = true;
                for(var operProds : operations.getOper_products()) {
                    for (var p : storage.values()) {
                        if (Objects.equals(operProds.getProd_type(), p.prod_item_type) && operProds.getProd_q() > p.prod_item_quantity) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    newMenu.put(item.getId(), item);
                }
            }
        }
        return newMenu;
    }
}
