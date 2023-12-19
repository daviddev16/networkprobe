package com.networkprobe.core.experimental;

import com.networkprobe.core.annotation.miscs.Feature;
import com.networkprobe.core.annotation.miscs.Obsolete;
import com.networkprobe.core.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static com.networkprobe.core.util.Validator.checkIsNullOrEmpty;
import static com.networkprobe.core.util.Validator.nonNull;

/**
 *  TEMPORÁRIO: Simplexer transforma funções detectadas no
 *  texto para FunctionTokens que podem ser interpretadas.
 *  Simplexer foi desenvolvido por mim há um tempo atrás
 *  apenas para testes.
 *  */
@Obsolete(reason = "Será substituido pela arquitetura de Json Functions.")
@Feature(notImplemented = false)
public class Simplexer {

    public static final char CHR_DOLLAR = '$';
    public static final char CHR_END = '}';
    public static final char CHR_START = '{';
    public static final char CHR_SEPARATOR = '|';

    private final List<FunctionToken> functionTokens = new ArrayList<>();
    private final StringBuilder buffer = new StringBuilder();
    private final String text;

    private FunctionToken currentToken;
    private int currentPos = 0;

    public Simplexer(String text) {
        this.text = Validator.checkIsNullOrEmpty(text, "text");
    }

    public void process() {
        if (text.isEmpty() || text.indexOf(CHR_DOLLAR) == -1)
            return;
        while (currentPos < text.length()) {
            if (checkValue(currentPos, CHR_DOLLAR)) {
                if (currentToken != null) {
                    throw new LexerException("Uma função foi escaneada sem o encerramento da função anterior." +
                            " Posição da função: " + currentPos);
                }
                int nextCharIndex = currentPos + 1;
                if (checkValue(nextCharIndex, CHR_START)) {
                    currentToken = new FunctionToken();
                    currentToken.setStartPosition(currentPos);
                }
            } else if (checkValue(currentPos, CHR_END)) {
                if (currentToken != null) {
                    String value = buffer.toString();
                    if (!value.isEmpty()) {
                        currentToken.getArguments().add(value);
                    }
                    currentToken.setEndPosition(currentPos);
                    getFunctionTokens().add(currentToken);
                    buffer.delete(0, buffer.length());
                    currentToken = null;
                }
            } else if (checkValue(currentPos, CHR_SEPARATOR)) {
                String value = buffer.toString();
                if (!value.isEmpty()) {
                    if (currentToken.getMethodName() == null) {
                        currentToken.setMethodName(value);
                    } else {
                        currentToken.getArguments().add(value);
                    }
                    buffer.delete(0, buffer.length());
                }
            } else if (!isCurrentCharacterEqualsTo(CHR_DOLLAR, CHR_START, CHR_END)) {
                if (currentToken != null && !checkValue(currentPos, '|'))
                    buffer.append(text.charAt(currentPos));
            }
            currentPos++;
        }
    }

    private boolean isCurrentCharacterEqualsTo(char... characters) {
        for (char c : characters) {
            if (text.charAt(currentPos) == c)
                return true;
        }
        return false;
    }

    private boolean checkValue(int pos, char chr) {
        int safeIndex = getSafeIndexValue(pos);
        return safeIndex != -1 && text.charAt(safeIndex) == chr;
    }

    private int getSafeIndexValue(int pos) {
        if (pos < 0 || pos > text.length())
            return -1;
        return pos;
    }

    public List<FunctionToken> getFunctionTokens() {
        return functionTokens;
    }

    public static String overlap(String original, FunctionToken token, String desired) {
        checkIsNullOrEmpty(original, "original");
        checkIsNullOrEmpty(desired, "desired");
        return original.replace(nonNull(token, "token").toRawValue(), desired);
    }

    public static boolean containsExpression(String content) {
        return content.contains("" + CHR_DOLLAR + CHR_START) &&
                content.indexOf(CHR_START) < content.indexOf(CHR_END);
    }

    public static final class FunctionToken {

        private final List<String> arguments = new ArrayList<>();

        private String methodName;
        private int startPosition;
        private int endPosition;

        public FunctionToken() {}

        public String getMethodName() {
            return methodName;
        }

        private void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public List<String> getArguments() {
            return arguments;
        }

        private void setStartPosition(int startPosition) {
            this.startPosition = startPosition;
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
            FunctionToken that = (FunctionToken) o;
            return that.toRawValue().equals(toRawValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, arguments, startPosition, endPosition);
        }

        @Override
        public String toString() {
            return "FunctionToken{" +
                    "methodName='" + methodName + '\'' +
                    ", arguments=" + arguments +
                    ", startPosition=" + startPosition +
                    ", endPosition=" + endPosition +
                    '}';
        }
    }

    public static final class LexerException extends RuntimeException
    {
        public LexerException(String message) { super(message); }
    }
    
}
