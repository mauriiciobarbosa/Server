package br.com.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import br.com.server.business.ServerBO;
import br.com.server.exception.SystemException;

public class DispatcherVO implements Runnable {
	private ServerBO serverBO;
	private ServerSocket serverSocket;
	private ArrayList<Runnable> workMans;
	private boolean value;
	
	public DispatcherVO(ServerBO serverBO, int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.serverBO = serverBO;
		value = true;
		workMans = new ArrayList<>();
	}
	
	@Override
	public void run() {
		Socket socket = null;
		WorkManVO workMan = null;
		int i=0;
		while (value) {
			try {
				socket = serverSocket.accept();
				workMan = new WorkManVO(this, socket);
				
				workMans.add(workMan);
				
				Thread thread = new Thread(workMan, "WorkMan" + ++i);
				thread.start();
				
				serverBO.receiveMessage("Novo cliente conectado");
				
			} catch(IOException e) {
				if (value)
					throw new SystemException("Erro ao tentar iniciar conexao com um determinado cliente!");
			}
		}
	}
	
	public void receiveMessage(String message) {
		serverBO.receiveMessage(message);
		this.sendMessage(message);
	}
	
	public void sendMessage(String message) {
		for (Runnable workMan : workMans) {
			((WorkManVO)workMan).sendMessage(message);
		}
	}
	
	synchronized public void removeWorkMan(WorkManVO workMan) {
		workMans.remove(workMan);
	}
	/*
	public boolean isClientsConnected() {
		return workMans.size() > 0;
	}
	*/
	synchronized public void  closeConnections() throws IOException {
		for (Runnable workMan : workMans) {
			((WorkManVO)workMan).sendMessage("end");
			((WorkManVO)workMan).close();
		}
		
		value = false;
		serverSocket.close();
	}
}
