package com.github.maxpesh.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GenerateAst2 {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        var path = Paths.get(outputDir).resolve(baseName) + ".java";
        try (var writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("""
                    package com.github.maxpesh.lox;
                    
                    import java.util.List;
                    
                    interface %s {
                        <R> R accept(Visitor<R> visitor);
                    }
                    """.formatted(baseName));
            defineVisitor(writer, baseName, types);
            // The AST classes.
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }
        }
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fields) {
        writer.println("""
                record %s(%s) implements %s {
                    @Override
                    public <R> R accept(Visitor<R> visitor) {
                        return visitor.visit%s(this);
                    }
                }
                """.formatted(className, fields, baseName, className + baseName));
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit%s(%s %s);".formatted(typeName + baseName, typeName, baseName.toLowerCase()));
            writer.println();
        }
        writer.println("}");
        writer.println();
    }
}
