package com.laundry.cli;

import java.util.Scanner;

import com.laundry.cli.menus.MainMenu;
public class Main {
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        MainMenu menu = new MainMenu(scanner);

        menu.display();

    }
}