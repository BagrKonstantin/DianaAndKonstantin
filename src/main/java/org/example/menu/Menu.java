package org.example.menu;

import java.util.HashSet;

public class Menu {
    HashSet<MenuItem> menu;

    public Menu() {
        menu = new HashSet<MenuItem>();
        menu.add(new MenuItem(0, 0, true));
        menu.add(new MenuItem(1, 2, true));
        menu.add(new MenuItem(2, 1, true));
        menu.add(new MenuItem(3, 3, true));
    }

    public HashSet<MenuItem> getMenu() {
        return menu;
    }
}
