package com.networkprobe.core.command.caching;

import com.networkprobe.core.reflection.ClassMapperHandler;

public class ProcessedResponseEntity extends CachedResponseEntity {

    private final ClassMapperHandler classMapperHandler;

    public ProcessedResponseEntity(String rawContent, boolean cached, ClassMapperHandler classMapperHandler) {
        super(rawContent, cached);
        this.classMapperHandler = classMapperHandler;
    }

    @Override
    public void cache() {
        /*simplexer.process();

        for (Simplexer.StringFunctionToken token : simplexer.getFunctionTokens()) {
            if (token.getMethodName().equals("getAddressOf")) {
                System.out.println(Simplexer.overlap(response, token, "192.168.1.2"));
            }
        }*/
    }

}
