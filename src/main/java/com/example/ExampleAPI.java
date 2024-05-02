package com.example;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ExampleAPI {
    private final static Path USER_HOME = Paths.get(System.getProperty("user.home"));
    public final static Path RUNELITE_DIR = USER_HOME.resolve(".runelite");
}
