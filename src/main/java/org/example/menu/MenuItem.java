package org.example.menu;

import java.io.Serializable;

public class MenuItem implements Serializable {
    int id;
    int recipe_id;
    boolean isAvailable;

    public MenuItem(int id, int recipe_id, boolean isAvailable) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "MenuItem" + id + ": recipeId: " + recipe_id;
    }
}
