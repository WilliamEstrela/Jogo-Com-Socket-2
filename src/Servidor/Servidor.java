package Servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nome;
	private Socket conecao;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;
	private static int questaoContador = 0;
	private static int playerContador = 0;
	private static int resultato = 0;
	private static int pontosPlayer1 = 0;
	private static int pontosPlayer2 = 0;

	public Servidor(Socket con) {
		this.conecao = con;
		try {
			in = con.getInputStream();
			inr = new InputStreamReader(in);
			bfr = new BufferedReader(inr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo para retornar um inteiro aliatorio
	 */
	public static int getNumeroAleatorio(Integer limite) {
		Random g = new Random();

		return g.nextInt(limite);
	}

	/**
	 * Retorna uma questão de soma e seta o resultado da questão na variavel global
	 * 'resultado'
	 * 
	 */

	public static String getQuestao() {
		int x, y, limite = 60;

		x = getNumeroAleatorio(limite);
		y = getNumeroAleatorio(limite);

		resultato = x + y;

		String questao = "Responda a soma dos números  " + x + " + " + y
				+ " ? \n Use o caracter ' @ ' para indentificar o inicio de uma resposta!";

		return questao;
	}

	/**
	 * Metodo para validar se a resposta enviada pelo cliente esta correta
	 * 
	 * 
	 */
	public Boolean respostaCorreta(String s) {
		int a;

		if (questaoContador == 0) {
			return false;
		}
		try {
			a = Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return a == resultato;

	}

	/**
	 * Método responsavel por iniciar a classe servidor com socket
	 * 
	 */
	public void run() {

		try {

			String mensagem;

			OutputStream ou = this.conecao.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bufwr = new BufferedWriter(ouw);
			clientes.add(bufwr);
			nome = mensagem = bfr.readLine();

			sendPanelNunber(bufwr);

			while (!"Sair".equalsIgnoreCase(mensagem) && mensagem != null) {

				if (playerContador == 2 && questaoContador == 0){
					sendQuestao(getQuestao());
				}

				mensagem = bfr.readLine();

				if (mensagem.length() > 1 && "@".equalsIgnoreCase(mensagem.substring(0, 1))) {

					//sendToAll(bufwr, mensagem);

					String respostaDigitada  = mensagem.substring(1, mensagem.length());

					if (respostaCorreta(respostaDigitada)) {
						System.out.println("Resposta correta!");
						sendPontos(bufwr);
						bufwr.write("limpar");
						bufwr.flush();
						validaPontos(nome);
					}

				} else {
					sendToAll(bufwr, mensagem);
				}

				System.out.println(mensagem);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void validaPontos(String nome) throws IOException{
		
		if (pontosPlayer1 == 5 || pontosPlayer2 == 5) {
			sendToAll(nome + " Parabens a Vitoria e Sua!");
		} else{
			sendQuestao(getQuestao());
		}
	}

	

	/**
	 *  Metado para enviar mensagens a todos os clientes conectados ao servidor
	 * 
	 */

	public void sendToAll(BufferedWriter bwSaida, String mensagem) throws IOException {
		BufferedWriter bwS, bwP = null;

		for (BufferedWriter bw : clientes) {
			bwS = bw;
			if (!(bwSaida == bwS)) {
				try {
					bw.write(nome + " -> " + mensagem + "\r\n");
					bw.flush();
				} catch (Exception e) {
					bwP = bwS;
				}
			}
		}
		if (bwP != null) {
			clientes.remove(bwP);
		}
	}

	public void sendToAll(String mensagem) throws IOException {
		BufferedWriter bwS, bwP = null;

		for (BufferedWriter bw : clientes) {

			try {
				bw.write("SERVER -> " + mensagem + "\r\n");
				bw.flush();
			} catch (Exception e) {
			}

		}

	}

	// Metado para atribuir pontuação aos jogadores

	public void sendPontos(BufferedWriter bwSaida) throws IOException {
		BufferedWriter bwS, bwP = null;
		int i = 0;
		for (BufferedWriter bw : clientes) {
			bwS = bw;
			i++;
			if (bwSaida == bwS)
				break;

		}
		for (BufferedWriter bw : clientes) {
			try {
				bw.write("*" + i + "\r\n");
				bw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (i == 1) {
			pontosPlayer1++;
		} else {
			pontosPlayer2++;
		}
	}

	public void sendPanelNunber(BufferedWriter bwSaida) throws IOException {

		bwSaida.write("#" + playerContador + "\r\n");
		bwSaida.flush();

	}

	/**
	 * Método responsável por enviar a 'questao' para todos os jogadores conectados
	 */
	public static void sendQuestao(String msg) throws IOException {
		BufferedWriter bwS, bwP = null;
		questaoContador++;

		for (BufferedWriter bw : clientes) {
			try {
				bw.write("SERVER->  " + questaoContador + " - " + msg + "\r\n");
				bw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Metodo responsavel por criar a conexao de socket
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		JLabel lblMessage = new JLabel("Porta do Servidor:");
		JTextField txtPorta = new JTextField("5566");
		Object[] texts = { lblMessage, txtPorta };
		JOptionPane.showMessageDialog(null, texts);

		try {
			Integer porta = Integer.parseInt(txtPorta.getText());

			server = new ServerSocket(porta);

			clientes = new ArrayList<BufferedWriter>();

			JOptionPane.showMessageDialog(null, " Servidor aberto na porta: " + porta);

			while (playerContador < 2) {
				System.out.println("Aguardando conexões.... necessario 2 players para jogar");

				//Recebe uma conexãoe coloca dentro de uma Thread assim iniciando-a
				Socket con = server.accept();
				Thread t = new Servidor(con);
				t.start();

				playerContador++;
				System.out.println("Novo cliente conectado");

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		
	}
}
