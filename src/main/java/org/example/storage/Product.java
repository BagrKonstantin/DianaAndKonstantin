package org.example.storage;

import jade.gui.VisualAIDList;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

public class Product {
    int prod_item_type;
    String prod_item_name;
    String prod_item_company;
    String prod_item_unit;
    Float prod_item_quantity;
    Float prod_item_cost;
    LocalDateTime prod_item_delivered;
    LocalDateTime prod_item_valid_until;
    public Product(JSONObject stuff) {
        this.prod_item_type = (Integer) stuff.get("prod_item_type");
        this.prod_item_name = (String) stuff.get("prod_item_name");
        this.prod_item_company = (String) stuff.get("prod_item_company");
        this.prod_item_unit = (String) stuff.get("prod_item_unit");
        this.prod_item_quantity = (Float) stuff.get("prod_item_quantity");
        this.prod_item_cost = (Float) stuff.get("prod_item_cost");
        this.prod_item_delivered = (LocalDateTime) stuff.get("prod_item_delivered");
        this.prod_item_valid_until = (LocalDateTime) stuff.get("prod_item_valid_until");
    }


}
