package com.networkprobe.core;

import com.networkprobe.core.annotation.miscs.Documented;

import java.net.Socket;

@Documented
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
     * @param receivedMessage  A informação/comando recebida do socket TCP/IP do cliente
     * @param connectedSocket  Socket TCP/IP do cliente que será informado pelo ClientHandler
     * @param clientHandler    ClientHandler que estará processando informação no momento da
     *                         execução do processSocketMessage.
     *
     * */
    String processSocketMessage(String receivedMessage, Socket connectedSocket, ClientHandler clientHandler);

}
