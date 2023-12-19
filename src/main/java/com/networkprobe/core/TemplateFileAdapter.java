package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;
import com.networkprobe.core.entity.base.ResponseEntity;

import java.io.File;

/**
 * FileTemplateAdapter permite que seja possível fazer a implementação de um Template
 * carregado através de um arquivo.
 * */
@Documented
public interface TemplateFileAdapter extends Template {

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

    /**
     * Re carrega as informações através do {@link TemplateFileAdapter#load(File, ResponseEntityFactory)}
     * com o arquivo mantido na implementação desse adapter.
     **/
    void reload();

}
