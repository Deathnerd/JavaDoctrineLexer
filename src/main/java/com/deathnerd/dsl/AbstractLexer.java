/**
 * Created by Wes Gilleland on 1/6/2017.
 */
package com.deathnerd.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

abstract public class AbstractLexer {
    /**
     * The original input string
     */
    private String input;
    /**
     * Scanned tokens
     */
    private ArrayList<LexerToken> tokens = new ArrayList<LexerToken>();
    /**
     * The current lexer position in the input string
     */
    private int position = 0;
    /**
     * The current peek of current lexer position
     */
    private int peek = 0;
    /**
     * The next token in the input
     */
    public LexerToken lookahead;
    /**
     * The last matched/seen token
     */
    public LexerToken token;
    /**
     * The regex used in scan
     */
    private Pattern regex;

    private HashMap<String, Integer> identifiers = new HashMap<String, Integer>();

    /**
     * <p>Sets the input data to be tokenized.</p>
     * <p>
     * The lexer is immediately reset and the new input is tokenized.
     * Any unprocessed tokens from any previous input are lost.
     * </p>
     *
     * @param input The input to be tokenized
     */
    public void setInput(String input) {
        this.input = input;
        this.tokens = new ArrayList<LexerToken>();

        this.reset();
        this.scan(input);
    }

    /**
     * <p>Resets the lexer</p>
     */
    public void reset() {
        this.lookahead = null;
        this.token = null;
        this.resetPeek();
        this.resetPosition();
    }

    /**
     * <p>Resets the peek pointer to 0</p>
     */
    public void resetPeek() {
        this.peek = 0;
    }

    /**
     * <p>Resets the lexer position on the input to the given position</p>
     */
    public void resetPosition() {
        this.position = 0;
    }

    /**
     * <p>Resets the lexer position on the input to the given position</p>
     *
     * @param position Position to place the lexical scanner
     */
    public void resetPosition(int position) {
        this.position = position;
    }

    /**
     * <p>Retrieve the original lexer's input until a given position.</p>
     *
     * @param position The position to stop at
     * @return The input from the beginning of the input until the given position
     */
    public String getInputUntilPosition(int position) {
        return this.input.substring(0, position);
    }

    /**
     * Checks whether a given token matches the current lookahead
     *
     * @param token The token type to look for
     * @return Whether the given token matches the current lookahead
     */
    public boolean isNextToken(int token) {
        return this.lookahead != null && this.lookahead.type == token;
    }

    /**
     * <p>Checks whether any of the given tokens matches the current lookahead.</p>
     *
     * @param tokens The tokens to check against
     * @return Whether any of the given tokens matches the current lookahead
     */
    public boolean isNextTokenAny(int[] tokens) {
        return this.lookahead != null && IntStream.of(tokens).anyMatch(s -> s == this.lookahead.type);
    }

    /**
     * <p>Checks whether any of the given tokens matches the current lookahead.</p>
     *
     * @param tokens The tokens to check against
     * @return Whether any of the given tokens matches the current lookahead
     */
    public boolean isNextTokenAny(ArrayList<Integer> tokens) {
        return this.lookahead != null && tokens.contains(this.lookahead.type);
    }

    /**
     * <p>Moves to the next token in the input string.</p>
     *
     * @return If false, then there are no more tokens. Otherwise, another token exists on lookahead
     */
    public boolean moveNext() {
        this.peek = 0;
        this.token = this.lookahead;
        if (this.tokens.size() == this.position - 1) {
            this.lookahead = this.tokens.get(this.position++);
        } else {
            this.lookahead = null;
        }

        return this.lookahead != null;
    }

    /**
     * <p>Instructs the lexer to skip input tokens until it sees a token with the given value</p>
     *
     * @param type The type of token to fast forward to
     */
    public void skipUntil(int type) {
        while (this.lookahead != null && this.lookahead.type != type) {
            this.moveNext();
        }
    }

    /**
     * <p>Checks if a given value is identical to the given token</p>
     *
     * @param value The value to check
     * @param token The token to check against
     * @return The value is of the token type
     */
    public boolean isA(String value, int token) {
        return this.getType(value) == token;
    }

    /**
     * <p>Moves the lookahead token forward</p>
     *
     * @return The next token or null if there are no more tokens ahead
     */
    public LexerToken peek() {
        if (this.tokens.size() == this.position + this.peek - 1) {
            return this.tokens.get(this.position + this.peek++);
        }
        return null;
    }

    /**
     * <p>Peeks at the next token, returns it and immediately resets the peek.</p>
     *
     * @return The next token or null if there are no more tokens ahead
     */
    public LexerToken glimpse() {
        LexerToken peek = this.peek();
        this.peek = 0;
        return peek;
    }

    /**
     * <p>Scans the input string for tokens.</p>
     * @param input A query string
     */
    protected void scan(String input) {
        if (this.regex == null) {
            String catchables = String.join(")|(", this.getCatchablePatterns());
            String noncatchables = String.join("|", this.getNonCatchablePatterns());
            int flags = Pattern.COMMENTS;
            for (int flag : this.getModifiers()) {
                flags |= flag;
            }
            String format = "(" + catchables + ")|" + noncatchables;
//            String format = "(%s)|%s".format(catchables, noncatchables);
            this.regex = Pattern.compile(format, flags);
        }

        Matcher matcher = this.regex.matcher(input);
        while(matcher.find()) {
            int type = this.getType(matcher.group());
            this.tokens.add(new LexerToken(
                    matcher.group(),
                    type,
                    matcher.start()
            ));
        }
    }

    /**
     * <p>Regex modifiers</p>
     *
     * @return An array of modifiers for a {@link java.util.regex.Pattern} object
     * @see java.util.regex.Pattern
     */
    abstract public int[] getModifiers();

    /**
     * <p>Lexical catchable patterns</p>
     *
     * @return An array of strings to be compiled as a regex pattern
     */
    abstract public String[] getCatchablePatterns();

    /**
     * <p>Lexical non-catchable patterns</p>
     *
     * @return An array of strings to be compiled as a regex pattern
     */
    abstract public String[] getNonCatchablePatterns();

    /**
     * <p>Retrieve the token type. Also processes the token value if necessary</p>
     *
     * @param value The value to get the type of
     * @return The token type
     */
    abstract public int getType(String value);

    public HashMap<String, Integer> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(HashMap<String, Integer> identifiers) {
        this.identifiers = identifiers;
    }
}
