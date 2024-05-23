package br.com.concorrente;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServerPerConnection {

	@SuppressWarnings("resource")
	public static void main(String args[]) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(8080);
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		while (true) {
			try {
				TrataReq tratador = new TrataReq(server.accept());
				tratador.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}