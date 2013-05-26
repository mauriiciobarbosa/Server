package br.com.server.business;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import br.com.server.exception.SystemException;
import br.com.server.exception.UserException;
import br.com.server.model.DispatcherVO;

public class ServerBO {
	/**
	 * Objeto responsavel por notificar outros objetos registrados, interessados em notificacao.
	 */
	private PropertyChangeSupport propertyChangeSupport;
	/**
	 * Objeto responsavel por receber requisicoes de conexao
	 */
	private DispatcherVO dispatcher;
	/**
	 * Metodo responsavel por fazer a validacao da porta.
	 * 
	 * @param port
	 * @return boolean
	 */
	private boolean validatePort(String port) {
		try {
			int portInt = Integer.parseInt(port);
			
			 // Faixa de portas tcp disponiveis.
			return (portInt > 0 && portInt < 65536);
		}catch(Exception ex) {
			return false;
		}
	}
	/**
	 * Construtor
	 */
	public ServerBO() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	/**
	 * Metodo responsavel por adicionar um objeto interessado em receber notificações
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	/**
	 * Metodo responsavel por "colocar o servidor no ar".
	 * 
	 * @param port
	 * @throws UserException
	 */
	public void openConnection(String port) throws UserException {
		if(!this.validatePort(port)) 
			throw new UserException("O numero de porta informado e invalido!");
			
			try {
				dispatcher = new DispatcherVO(this, Integer.parseInt(port));
				Thread thread = new Thread(dispatcher, "Dispatcher");
				thread.start();
			} catch (NumberFormatException e) {
				//Desconsidere.
			} catch (IOException e1) {
				throw new SystemException("Nao foi possivel iniciar o servidor");
			}
	}
	/**
	 * Metodo responsavel por enviar mensagens em broadcast.
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		dispatcher.sendMessage(message);
	}
	/**
	 * Metodo responsavel por receber novas mensagens.
	 * 
	 * @param message
	 */
	public void receiveMessage(String message) {
		propertyChangeSupport.firePropertyChange(null, null, message);
	}
	/**
	 * Metodo responsavel por verificar se ha clientes conectados.
	 * 
	 * @return
	 */
	/*
	public boolean checkConnectedClients() {
		return dispatcher.isClientsConnected();
	}
	*/
	/**
	 * Metodo responsavel por desconectar o servidor.
	 * 
	 * @throws SystemException
	 */
	public void closeConnection() throws SystemException {
		try {
			dispatcher.closeConnections();
		} catch (IOException e) {
			throw new SystemException("Houve um erro inesperado ao finalizar servidor");
		}
	}
}
