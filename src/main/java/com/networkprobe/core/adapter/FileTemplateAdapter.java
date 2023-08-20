package com.networkprobe.core.adapter;

import com.networkprobe.core.Template;
import com.networkprobe.core.entity.ResponseEntity;
import com.networkprobe.core.factory.ResponseEntityFactory;

import java.io.File;

/**
 * FileTemplateAdapter permite que seja possível fazer a implementação de um Template
 * carregado através de um arquivo.
 * */
public interface FileTemplateAdapter extends Template {

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
