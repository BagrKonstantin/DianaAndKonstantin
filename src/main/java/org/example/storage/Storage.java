package org.example.storage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;
import org.w3c.dom.ls.LSOutput;


public class Storage {
    public HashMap<Long, Product> storage;

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
}