package com.deathnerd.dsl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Wes Gilleland on 1/6/2017.
 */
class DqlLexerTest {
    @Test
    public void testThing(){
        DqlLexer lexer = new DqlLexer("SELECT u FROM MyProjectModelUser u WHERE u.age > 20");
        assertNotNull(lexer.glimpse());
    }

}