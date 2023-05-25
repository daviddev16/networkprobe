package com.networkprobe.core.command.caching;

import com.networkprobe.core.command.lexer.Simplexer;
import com.networkprobe.core.reflection.ClassMapperHandler;

import java.util.ArrayList;
import java.util.List;

public class ProcessedResponseEntity extends CachedResponseEntity {

    private final ClassMapperHandler classMapperHandler;
    private final List<Simplexer.StringFunctionToken> functionTokens;

    public ProcessedResponseEntity(String rawContent, boolean cached, ClassMapperHandler classMapperHandler) {
        super(rawContent, cached);
        this.classMapperHandler = classMapperHandler;
        this.functionTokens = createAllFunctionTokens(rawContent);
        cache();
    }

    @Override
    public void cache() {
        String parsedContent = parseAllTokensToValues();
        setCachedValue(CachedValue.createInstant(parsedContent));
    }

    private String parseAllTokensToValues() {
        String content = getRawContent();
        for (Simplexer.StringFunctionToken token : getFunctionTokens()) {
            List<String> arguments = token.getArguments();
            String methodName = token.getMethodName();
            String evaluatedValue = safe(getClassMapperHandler().execute(methodName, arguments));
            content = Simplexer.overlap(content, token, evaluatedValue);
        }
        return content;
    }

    private List<Simplexer.StringFunctionToken> createAllFunctionTokens(String rawContent) {
        Simplexer simplexer = new Simplexer(rawContent);
        simplexer.process();
        return new ArrayList<>(simplexer.getFunctionTokens());
    }

    private String safe(String evaluatedValue) {
        return (evaluatedValue != null && !evaluatedValue.isEmpty()) ? evaluatedValue : "<<empty_value_returned>>";
    }

    public ClassMapperHandler getClassMapperHandler() {
        return classMapperHandler;
    }

    public List<Simplexer.StringFunctionToken> getFunctionTokens() {
        return functionTokens;
    }
}