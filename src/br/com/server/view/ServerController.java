package br.com.server.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import br.com.server.business.ServerBO;
import br.com.server.exception.SystemException;
import br.com.server.exception.UserException;

/**
 * Classe responsável por fazer a comunicação entre camada de apresentação e camada de negócios
 * 
 * @author Mauricio Barbosa
 *
 */

public class ServerController implements PropertyChangeListener {
	/**
	 * View registrada para receber notificações.
	 */
	private ServerView 	registreredServerView;
	/**
	 * "Modelo" registrado para receber atualizações.
	 */
	private ServerBO 	registreredServerBO;
	/**
	 * Metodo responsavel por adicionar uma view interessada em receber atualizações.
	 * 
	 * @param serverBO
	 */
	public void addServerBO(ServerBO serverBO) {
		this.registreredServerBO = serverBO;
		serverBO.addPropertyChangeListener(this);
	}
	/**
	 * Metodo responsavel por adicionar um "modelo" interessado em receber atualizações.
	 * 
	 * @param serverView
	 */
	public void addServerView(ServerView serverView) {
		this.registreredServerView = serverView;
	}
	/**
	 * Metodo responsavel por solicitar a camada de negocios a abertura de conexao
	 * 
	 * @param port
	 * @throws UserException
	 */
	public void openConnection(String port) throws UserException {
		registreredServerBO.openConnection(port);
	}
	/**
	 * Metodo responsavel por solicitar a camada de negocios o envio de mensagens.
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		registreredServerBO.sendMessage(message);
	}
	/**
	 * Metodo responsavel por solicitar a camada de negocios a finalizacao da conexao.
	 * 
	 * @throws SystemException
	 */
	public void closeConnection() throws SystemException {
		registreredServerBO.closeConnection();
	}
	/**
	 * Metodo responsavel por verificar, junto a camada de negocios, se ha clientes conectados.
	 * 
	 * @return
	 */
	/*
	public boolean checkConnectedClients() {
		return registreredServerBO.checkConnectedClients(); 
	}
	*/
	/**
	 * Metodo responsavel por notificar a camada de apresentacao a chegada de novas mensagens.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		registreredServerView.modelPropertyChange(evt);
	}
	
}
