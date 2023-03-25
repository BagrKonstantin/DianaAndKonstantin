package org.example.storage;

import jade.gui.VisualAIDList;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Product {
    Long prod_item_type;
    String prod_item_name;
    String prod_item_company;
    String prod_item_unit;
    Double prod_item_quantity;
    Double prod_item_cost;
    LocalDateTime prod_item_delivered;
    LocalDateTime prod_item_valid_until;

    public Product(JSONObject stuff) {
        this.prod_item_type = (Long) stuff.get("prod_item_type");
        this.prod_item_name = (String) stuff.get("prod_item_name");
        this.prod_item_company = (String) stuff.get("prod_item_company");
        this.prod_item_unit = (String) stuff.get("prod_item_unit");
        this.prod_item_quantity = Double.valueOf(stuff.get("prod_item_quantity").toString());
        this.prod_item_cost = Double.valueOf(stuff.get("prod_item_cost").toString());
        this.prod_item_delivered = LocalDateTime.parse((String) stuff.get("prod_item_delivered"));
        this.prod_item_valid_until = LocalDateTime.parse((String) stuff.get("prod_item_valid_until"));
    }
}
