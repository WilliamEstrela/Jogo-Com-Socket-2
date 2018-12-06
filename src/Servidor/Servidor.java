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
	 * Retorna um inteiro aleatorio
	 */
	public static int getNumeroAleatorio(Integer limite){
		Random g = new Random();

		return g.nextInt(limite);
	}

	/**
	 * Retorna uma questao de de + e seta o resultado da questao na variavel global 'resultado'
	 */
	public static String getQuestao() {
		int x, y, limite=60;

		x = getNumeroAleatorio(limite);
		y = getNumeroAleatorio(limite);
		
		resultato = x + y;
		
		String questao = "Responda a soma dos numeros  " + x + " + " + y + " ?";

		return questao;
	}


	/**
	 * Metodo validador de resposta
	 * @param s
	 * @return
	 */
	public Boolean RespostaCorreta(String s) {
		int a;

		if (questaoContador == 0){
			return false;
		}
		try {
			a = Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return a == resultato;

	}

	/*
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

				if (playerContador == 2 && questaoContador == 0)
					sendQuestao(getQuestao());
				mensagem = bfr.readLine();

				if (mensagem.length() > 1 && "@".equalsIgnoreCase(mensagem.substring(0, 1))) {
					sendToAll(bufwr, mensagem);
					if (RespostaCorreta(mensagem.substring(1, mensagem.length()))) {
						System.out.println("Resposta correta!");
						sendPontos(bufwr);
						
						if (pontosPlayer1 == 5 || pontosPlayer2 == 5) {
							sendToAll(nome + " Parab�ns a Vitoria e Sua!");
						} else
							
							sendQuestao(getQuestao());
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

	// Metado para enviar mensagens a todos

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

	// Fun��o para adribuir a pontua��o

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
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("5566");
			Object[] texts = { lblMessage, txtPorta };
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, " Servidor conectado na porta: " + txtPorta.getText());

			while (playerContador < 2) {
				System.out.println("Aguardando conex�es....");
				System.out.println(" Necessario 2 ou mas players para jogar!..");
				Socket con = server.accept();
				playerContador++;
				System.out.println("Novo cliente conectado");
				Thread t = new Servidor(con);
				t.start();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}// fecha main
} // fecha Classe
