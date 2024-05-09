package br.com.concorrente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class TrataReq extends Thread {
	private Socket conexao;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public TrataReq(Socket con) {
		conexao = con;
		try {
			System.err.println("passei aqui 4");
			input = new ObjectInputStream(conexao.getInputStream());
			output = new ObjectOutputStream(conexao.getOutputStream());
			System.err.println("passei aqui 5");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			DataRequest data = (DataRequest) input.readObject();
			ProcessReq processor = new ProcessReq();
			List<Map.Entry<String, Double>> recommendations = processor.ProcessDataReq(data.getUser(),
					data.getNumRecomendations());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
			writer.write("reposta do servidor");

			System.err.println("Resposta enviada para: " + conexao.getInetAddress());
		} catch (IOException | ClassNotFoundException e) {
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