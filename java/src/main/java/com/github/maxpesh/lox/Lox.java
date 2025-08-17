package com.github.maxpesh.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lox {
    static boolean hadError;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runPrompt() throws IOException {
        var input = new BufferedReader(new InputStreamReader(System.in));
        for (; ; ) {
            System.out.print("> ");
            var line = input.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void runFile(String path) throws IOException {
        var bytes = Files.readAllBytes(Path.of(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        }
    }

    private static void run(String src) {
        var lexer = new Lexer(src);
        var tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String msg) {
        report(line, "", msg);
    }

    private static void report(int line, String where, String msg) {
        System.out.printf("[line %d] Error%s: %s%n", line, where, msg);
        hadError = true;
    }
}