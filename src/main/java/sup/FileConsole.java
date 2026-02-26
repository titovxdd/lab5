package sup;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class FileConsole implements Console {
    private final Scanner input;

    public FileConsole(Scanner input) {
        this.input = input;
    }

    @Override
    public void print(Object obj) {
    }

    @Override
    public void println(Object obj) {
    }

    @Override
    public void printError(Object obj) {
        System.err.println("Error: " + obj);
    }

    @Override
    public void printTable(Object obj1, Object obj2) {
    }

    @Override
    public String readln() {
        return input.nextLine();
    }
}
