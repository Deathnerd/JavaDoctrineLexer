package com.deathnerd.dsl;

public class LexerToken {
    public LexerToken(String value, int type, int positon) {
        this.value = value;
        this.type = type;
        this.positon = positon;
    }

    /**
     * The value of the token from the input string
     */
    public String value = "";
    /**
     * The type of token
     */
    public int type;
    /**
     * The position of the token in the input string
     */
    public int positon;
}
