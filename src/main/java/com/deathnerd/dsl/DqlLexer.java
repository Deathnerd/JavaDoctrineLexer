package com.deathnerd.dsl;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Wes Gilleland on 1/6/2017.
 */
public class DqlLexer extends AbstractLexer {
    private HashMap<String, Integer> identifiers = new HashMap<String, Integer>() {{
        put("T_NONE", 1);
        put("T_INTEGER", 2);
        put("T_STRING", 3);
        put("T_INPUT_PARAMETER", 4);
        put("T_FLOAT", 5);
        put("T_CLOSE_PARENTHESIS", 6);
        put("T_OPEN_PARENTHESIS", 7);
        put("T_COMMA", 8);
        put("T_DIVIDE", 9);
        put("T_DOT", 10);
        put("T_EQUALS", 11);
        put("T_GREATER_THAN", 12);
        put("T_LOWER_THAN", 13);
        put("T_MINUS", 14);
        put("T_MULTIPLY", 15);
        put("T_NEGATE", 16);
        put("T_PLUS", 17);
        put("T_OPEN_CURLY_BRACE", 18);
        put("T_CLOSE_CURLY_BRACE", 19);
        put("T_IDENTIFIER", 100);
        put("T_ALL", 101);
        put("T_AND", 102);
        put("T_ANY", 103);
        put("T_AS", 104);
        put("T_ASC", 105);
        put("T_AVG", 106);
        put("T_BETWEEN", 107);
        put("T_BOTH", 108);
        put("T_BY", 109);
        put("T_CASE", 110);
        put("T_COALESCE", 111);
        put("T_COUNT ", 112);
        put("T_DELETE", 113);
        put("T_DESC", 114);
        put("T_DISTINCT", 115);
        put("T_ELSE", 116);
        put("T_EMPTY ", 117);
        put("T_END", 118);
        put("T_ESCAPE", 119);
        put("T_EXISTS", 120);
        put("T_FALSE", 121);
        put("T_FROM", 122);
        put("T_GROUP", 123);
        put("T_HAVING", 124);
        put("T_HIDDEN", 125);
        put("T_IN", 126);
        put("T_INDEX", 127);
        put("T_INNER", 128);
        put("T_INSTANCE", 129);
        put("T_IS", 130);
        put("T_JOIN", 131);
        put("T_LEADING", 132);
        put("T_LEFT", 133);
        put("T_LIKE", 134);
        put("T_MAX", 135);
        put("T_MEMBER", 136);
        put("T_MIN", 137);
        put("T_NOT", 138);
        put("T_NULL", 139);
        put("T_NULLIF", 140);
        put("T_OF", 141);
        put("T_OR", 142);
        put("T_ORDER", 143);
        put("T_OUTER", 144);
        put("T_SELECT", 145);
        put("T_SET", 146);
        put("T_SOME", 147);
        put("T_SUM", 148);
        put("T_THEN", 149);
        put("T_TRAILING", 150);
        put("T_TRUE", 151);
        put("T_UPDATE", 152);
        put("T_WHEN", 153);
        put("T_WHERE", 154);
        put("T_WITH", 155);
        put("T_PARTIAL", 156);
        put("T_NEW", 157);
    }};

    DqlLexer(String input) {
        this.setInput(input);
    }

    @Override
    public int[] getModifiers() {
        return new int[]{Pattern.CASE_INSENSITIVE};
    }

    @Override
    public String[] getCatchablePatterns() {
        return new String[]{
                "[a-z_\\\\][a-z0-9_\\:\\\\]*[a-z0-9_]{1,}",
                "(?:[0-9]+(?:[\\.][0-9]+)*)(?:e[+-]?[0-9]+)",
                "'(?:[^']|'')*'",
                "\\?[0-9]*|:[a-z_][a-z0-9_]*"
        };
    }

    @Override
    public String[] getNonCatchablePatterns() {
        return new String[]{"\\s+", "(.)"};
    }

    @Override
    public int getType(String value) {
        int type = this.identifiers.get("T_NONE");

        if (Utils.isNumeric(value)) { // Recognize numeric values
            if (value.indexOf('.') != -1 || value.toLowerCase().indexOf('e') != -1) {
                return this.identifiers.get("T_FLOAT");
            }

            return this.identifiers.get("T_INTEGER");

        } else if (value.charAt(0) == '\'') { // Recognize Quoted strings
            // TODO pass by reference not supported in Java
            value = value.substring(1, value.length() - 2).replace("''", "'");
            return this.identifiers.get("T_STRING");
        } else if (String.valueOf(value.charAt(0)).matches("[a-zA-Z]") || value.charAt(0) == '_') { // Recognize identifiers
            String name = String.format("T_%s", value.toUpperCase());
            if (this.identifiers.containsKey(name)) {
                return this.identifiers.get(name);
            }
            return this.identifiers.get("T_IDENTIFIER");
        } else if (value.charAt(0) == '?' || value.charAt(0) == ':') { // Recognize input parameters
            return this.identifiers.get("T_INPUT_PARAMETER");
        } else {
            // Recognise symbols
            switch (value) {
                case ("."):
                    return this.identifiers.get("T_DOT");
                case (","):
                    return this.identifiers.get("T_COMMA");
                case ("("):
                    return this.identifiers.get("T_OPEN_PARENTHESIS");
                case (")"):
                    return this.identifiers.get("T_CLOSE_PARENTHESIS");
                case ("="):
                    return this.identifiers.get("T_EQUALS");
                case (">"):
                    return this.identifiers.get("T_GREATER_THAN");
                case ("<"):
                    return this.identifiers.get("T_LOWER_THAN");
                case ("+"):
                    return this.identifiers.get("T_PLUS");
                case ("-"):
                    return this.identifiers.get("T_MINUS");
                case ("*"):
                    return this.identifiers.get("T_MULTIPLY");
                case ("/"):
                    return this.identifiers.get("T_DIVIDE");
                case ("!"):
                    return this.identifiers.get("T_NEGATE");
                case ("{"):
                    return this.identifiers.get("T_OPEN_CURLY_BRACE");
                case ("}"):
                    return this.identifiers.get("T_CLOSE_CURLY_BRACE");
                default:
                    // D nothing
                    break;
            }
        }

        return type;
    }
}
