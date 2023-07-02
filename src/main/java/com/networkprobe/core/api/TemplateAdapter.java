package com.networkprobe.core.api;

import java.io.File;

/**
 * TemplateAdapter permite que seja possível fazer a implementação de um Template
 * carregado através de um arquivo.
 * */
public interface TemplateAdapter extends Template {

    /**
     * Faz o carregamento do {@link Template} através de um arquivo e define
     * qual será o {@link ResponseEntityFactory} que será utilizado para definir
     * os tipos de {@link ResponseEntity} encontrados nos comandos do Template no
     * arquivo.
     *
     * @param file Arquivo que será utilizado para alimentar o {@link Template}.
     * @param responseEntityFactory Factory utilizado para definir quais {@link ResponseEntity}
     *                              serão utilizados quando for feito o mapeamento dos comandos.
     *
     * */
    void load(File file, ResponseEntityFactory responseEntityFactory);

}
