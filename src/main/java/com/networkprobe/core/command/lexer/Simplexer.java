package com.networkprobe.core.command.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static com.networkprobe.core.util.Validator.*;

public class Simplexer {

    public static final char CHR_DOLLAR = '$';
    public static final char CHR_END = '}';
    public static final char CHR_START = '{';
    public static final char CHR_SEPARATOR = '|';

    private final List<StringFunctionToken> functionTokens = new ArrayList<>();
    private final StringBuilder buffer = new StringBuilder();
    private final String text;

    private StringFunctionToken currentToken;
    private int currentPos = 0;

    public Simplexer(String text) {
        this.text = Objects.requireNonNull(text, "Text cannot be null.");
    }

    public void process() {
        if (text.isEmpty() || text.indexOf(CHR_DOLLAR) == -1)
            return;
        while (currentPos < text.length()) {
            if (checkValue(currentPos, CHR_DOLLAR)) {
                processDollarCharacter();
            } else if (checkValue(currentPos, CHR_END)) {
                processEndCharacter();
            } else if (checkValue(currentPos, CHR_SEPARATOR)) {
                processSeparator();
            } else if (skipEqualsTo(CHR_DOLLAR, CHR_START, CHR_END)){
                processLetter();
            }
            currentPos++;
        }
    }

    private void processSeparator() {
        String value = buffer.toString();
        if (value.isEmpty()) {
            return;
        }
        if (currentToken.getMethodName() == null) {
            currentToken.setMethodName(value);
        } else {
            currentToken.getArguments().add(value);
        }
        clearStringBuffer();
    }

    private void processLetter() {
        if (currentToken != null && !checkValue(currentPos, '|'))
            appendToBuffer(text.charAt(currentPos));
    }

    private void processEndCharacter() {
        if (currentToken != null) {
            String value = buffer.toString();
            if (!value.isEmpty()) {
                currentToken.getArguments().add(value);
            }
            currentToken.setEndPosition(currentPos);
            getFunctionTokens().add(currentToken);
            clearStringBuffer();
            currentToken = null;
        }
    }

    private void processDollarCharacter() {
        if (currentToken != null) {
            throw new LexerException("Uma função foi escaneada sem o encerramento da função anterior." +
                    " Posição da função: " + currentPos);
        }
        int nextCharIndex = currentPos + 1;
        if (checkValue(nextCharIndex, CHR_START)) {
            currentToken = new StringFunctionToken();
            currentToken.setStartPosition(currentPos);
        }
    }

    private boolean checkValue(int pos, char chr) {
        int safeIndex = getSafeIndexValue(pos);
        if (safeIndex == -1)
            return false;
        return text.charAt(safeIndex) == chr;
    }

    private void appendToBuffer(char character) {
        buffer.append(character);
    }

    private void clearStringBuffer() {
        buffer.delete(0, buffer.length());
    }

    private boolean skipEqualsTo(char... chrs) {
        for (char chr : chrs) {
            if (chr == text.charAt(currentPos)) {
                return false;
            }
        }
        return true;
    }

    private int getSafeIndexValue(int pos) {
        if (pos < 0 || pos > text.length())
            return -1;
        return pos;
    }

    public List<StringFunctionToken> getFunctionTokens() {
        return functionTokens;
    }

    public String getText() {
        return text;
    }

    public static String overlap(String original, StringFunctionToken token, String desired) {
        checkIsNullOrEmpty(original, "original");
        checkIsNotNull(token, "token");
        checkIsNullOrEmpty(desired, "desired");
        return original.replace(token.toRawValue(), desired);
    }

    public static boolean containsExpression(String content) {
        return content.contains("" + CHR_DOLLAR + CHR_START) &&
                content.indexOf(CHR_START) < content.indexOf(CHR_END);
    }

    public static final class StringFunctionToken {

        private List<String> arguments = new ArrayList<>();

        private String methodName;
        private int startPosition;
        private int endPosition;

        public StringFunctionToken() {}

        public String getMethodName() {
            return methodName;
        }

        private void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public List<String> getArguments() {
            return arguments;
        }

        private void setArguments(List<String> arguments) {
            this.arguments = arguments;
        }

        public int getStartPosition() {
            return startPosition;
        }

        private void setStartPosition(int startPosition) {
            this.startPosition = startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }

        private void setEndPosition(int endPosition) {
            this.endPosition = endPosition;
        }

        public String toRawValue() {
            StringJoiner joiner = new StringJoiner("|");
            getArguments().forEach(joiner::add);
            return String.format("${%s|%s}", getMethodName(), joiner);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringFunctionToken that = (StringFunctionToken) o;
            return that.toRawValue().equals(toRawValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, arguments, startPosition, endPosition);
        }

        @Override
        public String toString() {
            return "StringFunctionToken{" +
                    "methodName='" + methodName + '\'' +
                    ", arguments=" + arguments +
                    ", startPosition=" + startPosition +
                    ", endPosition=" + endPosition +
                    '}';
        }
    }

    public static final class LexerException extends RuntimeException {

        public LexerException() { super(); }

        public LexerException(String message) {
            super(message);
        }

    }
}
