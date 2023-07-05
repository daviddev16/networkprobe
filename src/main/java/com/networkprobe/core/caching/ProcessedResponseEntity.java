package com.networkprobe.core.caching;

import com.networkprobe.core.ExceptionHandler;
import com.networkprobe.core.command.Simplexer;
import com.networkprobe.core.ClassMapperHandler;
import com.networkprobe.core.exception.ExecutionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Essa classe processa a informação de uma função vinda do template e
 * armazena em cache para ser usado em uma próxima requisição.
 * */
public class ProcessedResponseEntity extends CachedResponseEntity {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessedResponseEntity.class);

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