package com.github.xjln.interpreter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tokenhandler {

    private final List<Token> tokens;
    private int index;

    public Tokenhandler(List<Token> tokens){
        this.tokens = tokens;
        index = -1;
    }

    /**
     * increases index by one and gets the token with the increased index
     * @return next Token
     * @throws RuntimeException if no next Token available
     */
    public Token next() throws RuntimeException {
        if(!hasNext()) throw new RuntimeException("expected Token got nothing ");
        index++;
        return tokens.get(index);
    }

    /**
     * gets the Token with the current index
     * @return current Token
     * @throws RuntimeException if index isn't valid
     */
    public Token current() throws RuntimeException {
        if(!isValid()) throw new RuntimeException("expected Token got nothing");
        return tokens.get(index);
    }

    /**
     * decrease index by one and gets the token with the decreased index
     * @return last Token
     * @throws RuntimeException if index isn't valid
     */
    public Token last() throws RuntimeException {
        index--;
        if(!isValid()) throw new RuntimeException("expected Token got nothing");
        return tokens.get(index);
    }

    /**
     * check if a next Token is available
     * @return next Token available
     */
    public boolean hasNext(){
        return index + 1 < tokens.size();
    }

    /**
     * checks if the current index is valid
     * @return index is valid
     */
    public boolean isValid(){
        return index < tokens.size() && index > -1;
    }

    /**
     * checks if the Tokenhandler contains Tokens
     * @return is empty
     */
    public boolean isEmpty(){
        return tokens.size() == 0;
    }

    /**
     * sets the index to -1
     */
    public void toFirst(){
        index = -1;
    }

    /**
     * get the Tokens in Brackets
     * @return a new Tokenhandler with the tokens
     * @throws RuntimeException if current index isn't valid or there are no opening or closing brackets
     */
    public Tokenhandler getInBracket() throws RuntimeException {
        if(!isValid()) throw new RuntimeException("expected left bracket got nothing");
        Token current = tokens.get(index);
        if(!Set.of("(", "[").contains(current.s())) throw new RuntimeException("expected left bracket got " + tokens.get(index).s());

        String openingBracket = current.s();
        String closingBracket = openingBracket.equals("(") ? ")" : openingBracket.equals("[") ? "]" : "}";
        List<Token> tokenList = new ArrayList<>();
        int i = 1;

        while (i > 0 && hasNext()){
            current = next();
            if(current.s().equals(closingBracket)) i--;
            else if(current.s().equals(openingBracket)) i++;
            if(i > 0) tokenList.add(current);
        }

        if(i > 0) throw new RuntimeException("expected right bracket got nothing");
        return new Tokenhandler(tokenList);
    }

    /**
     * assert Token with the given content for the next index and increase the index by one
     * @param string the content assert for next Token
     * @return the Token with the increased index
     * @throws RuntimeException if it hasn't next Token or next Token hasn't the given content
     */
    public Token assertToken(@NotNull String string) throws RuntimeException {
        if(!hasNext()) throw new RuntimeException("expected " + string + " got nothing");
        Token token = next();
        if(!token.s().equals(string)) throw new RuntimeException("expected " + string + " got " + token.s());
        return token;
    }

    /**
     * assert Token to has a content equal to one of the given Strings and increase the index by one
     * @param strings the contents one is asset for next Token
     * @return the Token with the increased index
     * @throws RuntimeException if it hasn't next Token or next Token hasn't one the given contents
     */
    public Token assertToken(@NotNull String...strings) throws RuntimeException {
        if(!hasNext()) throw new RuntimeException("expected one of " + arrayToString(strings) + " got nothing");
        Token token = next();

        for(String str:strings) if(token.s().equals(str)) return token;

        throw new RuntimeException("expected one of " + arrayToString(strings) + " got " + token.s());
    }

    /**
     * assert Token with the given Type for the next index and increase the index by one
     * @param type the type assert for next Token
     * @return the Token with the increased index
     * @throws RuntimeException if it hasn't next Token or next Token hasn't the given type
     */
    public Token assertToken(@NotNull Token.Type type) throws RuntimeException {
        if(!hasNext()) throw new RuntimeException("expected " + type.toString() + " got nothing");
        Token token = next();
        if(token.t() != type) throw new RuntimeException("expected " + type.toString() + " got " + token.t().toString());
        return token;
    }

    /**
     * assert Token to has a type equal to one of the given types and increase the index by one
     * @param types the types one is asset for next Token
     * @return the Token with the increased index
     * @throws RuntimeException if it hasn't next Token or next Token hasn't one the given types
     */
    public Token assertToken(@NotNull Token.Type...types) throws RuntimeException {
        if(!hasNext()) throw new RuntimeException("expected one of " + arrayToString(types) + " got nothing");
        Token token = next();

        for(Token.Type t:types) if(token.t() == t) return token;

        throw new RuntimeException("expected one of " + arrayToString(types) + " got " + token.t().toString());
    }

    /**
     * assert content for a given Token
     * @param token Token who assert content for
     * @param string content to assert
     * @throws RuntimeException if the given Token hasn't the given content
     */
    public static void assertToken(@NotNull Token token,@NotNull String string) throws RuntimeException {
        if(!token.s().equals(string)) throw new RuntimeException("expected " + string + " got " + token.s());
    }

    /**
     * assert type for a given Token
     * @param token Token who assert type for
     * @param type type to assert
     * @throws RuntimeException if the given Token hasn't the given type
     */
    public static void assertToken(@NotNull Token token,@NotNull  Token.Type type) throws RuntimeException {
        if(token.t() != type) throw new RuntimeException("expected " + type.toString() + " got " + token.t().toString());
    }

    /**
     * concatenate the strings of the given string array to one string, separated by commas
     * @param sa the String array to String
     * @return all strings of the given array putt in one String
     */
    private String arrayToString(@NotNull String[] sa){
        StringBuilder sb = new StringBuilder();

        for(String s:sa) sb.append(s).append(", ");

        sb.deleteCharAt(sb.length() - 2);
        return sb.toString();
    }

    /**
     * concatenate the types of the given type array to one string, separated by commas
     * @param ta the type array to String
     * @return all types of the given array putt in one String
     */
    private String arrayToString(@NotNull Token.Type[] ta){
        StringBuilder sb = new StringBuilder();

        for(Token.Type t:ta) sb.append(t.toString()).append(", ");

        sb.deleteCharAt(sb.length() - 2);
        return sb.toString();
    }
}
