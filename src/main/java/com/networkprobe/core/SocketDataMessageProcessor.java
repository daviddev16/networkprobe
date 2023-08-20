package com.networkprobe.core;

import java.net.Socket;

import com.networkprobe.core.*;

public interface SocketDataMessageProcessor {

    /**
     * processSocketMessage recebe a informação do cliente TCP/IP, processa a informação
     * de rede e retorna de volta para ser enviado para o cliente. <i>Informações de rota
     * não são processadas pelo ClientHandler por que a informação de rota são definidas
     * individualmente por comando </i>
     *
     * @return Retorna o dado processado pela implementação do SocketDataMessageProcessor
     * que erá enviado para o {@link ClientHandler} do cliente TCP/IP.
     *
     * @param message A informação/comando recebida do socket TCP/IP do cliente
     * @param socket Socket TCP/IP do cliente que será informado pelo ClientHandler
     *
     * */
    String processSocketMessage(String message, Socket socket);
}
