package br.com.concorrente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class TrataReq extends Thread {
	private Socket conexao;
	private ObjectOutputStream output;

	public TrataReq(Socket con) {
		conexao = con;
		try {
			output = new ObjectOutputStream(conexao.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			ProcessReq processor = new ProcessReq();
			processor.ProcessDataReq("A3UH4UZ4RSVO82", 3);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
			writer.write("reposta do servidor");

			System.err.println("Resposta enviada para: " + conexao.getInetAddress());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				conexao.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}