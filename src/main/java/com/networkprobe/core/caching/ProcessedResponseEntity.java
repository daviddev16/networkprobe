package com.networkprobe.core.caching;

import com.networkprobe.core.experimental.Simplexer;
import com.networkprobe.core.ClassMapperHandler;

import java.util.List;

/**
 * ProcessedResponseEntity processa a informação de uma função vinda do template e
 * armazena em cache para ser usado em uma próxima requisição.
 * */
public class ProcessedResponseEntity extends CachedResponseEntity {

    private final List<Simplexer.StringFunctionToken> functionTokens;

    public ProcessedResponseEntity(String rawContent, boolean cached) {
        super(rawContent, cached);
        this.functionTokens = createAllFunctionTokens(rawContent);
        cache();
    }

    private List<Simplexer.StringFunctionToken> createAllFunctionTokens(String rawContent) {
        Simplexer simplexer = new Simplexer(rawContent);
        simplexer.process();
        return simplexer.getFunctionTokens();
    }

    private String parseAllTokensToValues() {
        String content = getRawContent();
        for (Simplexer.StringFunctionToken token : getFunctionTokens())
        {
            String evaluatedValue = ClassMapperHandler.getInstance()
                    .execute(token.getMethodName(), token.getArguments());
            content = Simplexer.overlap(content, token, safe(evaluatedValue));
        }
        return content;
    }

    @Override
    public void cache() {
        String parsedContent = parseAllTokensToValues();
        setCachedValue(CachedValue.createInstant(parsedContent));
    }

    private String safe(String evaluatedValue) {
        return (evaluatedValue != null && !evaluatedValue.isEmpty())
                ? evaluatedValue : "<<empty_value_returned>>";
    }
    public List<Simplexer.StringFunctionToken> getFunctionTokens() {
        return functionTokens;
    }
}