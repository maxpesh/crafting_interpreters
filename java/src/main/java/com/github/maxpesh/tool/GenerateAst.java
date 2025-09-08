package com.github.maxpesh.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
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
                    
                    abstract class %s {\
                    """.formatted(baseName));
            // The AST classes.
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }
            writer.println("}");
        }
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        String[] fields = fieldList.split(", ");

        writer.println("""
                    static class %s extends %s {
                        %s(%s) {\
                """.formatted(className, baseName, className, fieldList));
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this.%s = %s;".formatted(name, name));
        }
        writer.println("""
                        }
                """);
        for (String field : fields) {
            writer.println("        final %s;".formatted(field));
        }
        writer.println("    }");
        writer.println();
    }
}
