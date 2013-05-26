package br.com.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class WorkManVO implements Runnable {
	private Socket socketClient;
	private DispatcherVO dispatcher;
	private BufferedReader reader;
	private PrintStream writer;
	private boolean value;
	
	public WorkManVO(DispatcherVO dispatcher, Socket socketClient) throws IOException {
		this.socketClient = socketClient;
		this.dispatcher = dispatcher;
		
		value = true;
		
		reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		writer = new PrintStream(socketClient.getOutputStream());
	}

	public void close() {
		value = false;
	}
	
	@Override
	public void run() {
		String message = null;
		try {
			while (value) {

				message = reader.readLine();
				
				if (!message.equalsIgnoreCase("end"))
					dispatcher.receiveMessage(message);
				else {
					dispatcher.receiveMessage("Fulano saiu");
					dispatcher.removeWorkMan(this);
					value = false;
				}
			}
		} catch (NullPointerException e) {
			//Ocorre quando o servidor eh desconectado. IGNORAR!
		} catch (IOException e) {
				writer.println("A mensagem nao pode ser enviada ao servidor!");
		} finally {
			this.closeConnection();
		}
	}
	
	public void sendMessage(String message) {
		writer.println(message);
	}
	
	private void closeConnection() {		
		try {
			//Desaloca os recursos.
			reader.close();
			writer.close();
			socketClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
