package Cliente;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
//import net.miginfocom.swing.MigLayout;
import java.awt.GridLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Cliente extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField textoMensagem;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	private JTextField textoIp;
	private JTextField textoPorta;
	private JTextField textoNome;
	private JPanel painelPlacar;
	private JProgressBar quadroProgresso1;
	private JProgressBar quadroProgresso2;
	private JLabel lbl1;
	private JLabel lbl2;

	public Cliente() throws IOException {
		JLabel lblMessage = new JLabel("Verificar!");
		textoIp = new JTextField("127.0.0.1");
		textoPorta = new JTextField("5566");
		textoNome = new JTextField("Cliente");
		Object[] texts = { lblMessage, textoIp, textoPorta, textoNome };
		JOptionPane.showMessageDialog(null, texts);
		
		pnlContent = new JPanel();
		textoMensagem = new JTextField(20);
		textoMensagem.setBounds(56, 361, 352, 34);
		lblMsg = new JLabel("Mensagem");
		lblMsg.setBounds(20, 342, 75, 14);
		btnSair = new JButton("Sair");
		btnSair.setBounds(102, 406, 88, 23);
		btnSair.setToolTipText("Sair do Chat");
		btnSair.addActionListener(this);
		textoMensagem.addKeyListener(this);
		JScrollPane scroll = new JScrollPane();
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 133, 445, 207);
		pnlContent.setLayout(null);
		pnlContent.add(scroll);
		texto = new JTextArea(10, 20);
		scroll.setViewportView(texto);
		texto.setEditable(false);
		texto.setBackground(new Color(240, 240, 240));
		texto.setLineWrap(true);
		texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		pnlContent.add(lblMsg);
		pnlContent.add(textoMensagem);
		pnlContent.add(btnSair);
		pnlContent.setBackground(Color.LIGHT_GRAY);
		textoMensagem.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		setTitle(textoNome.getText());
		setContentPane(pnlContent);
		btnSend = new JButton("Enviar");
		btnSend.setBounds(280, 406, 81, 23);
		btnSend.setToolTipText("Enviar Mensagem");
		btnSend.addActionListener(this);
		btnSend.addKeyListener(this);
		pnlContent.add(btnSend);
		
		painelPlacar = new JPanel();
		painelPlacar.setBounds(10, 11, 445, 123);
		pnlContent.add(painelPlacar);
		
		quadroProgresso1 = new JProgressBar();
		quadroProgresso1.setForeground(Color.GREEN);
		quadroProgresso1.setOrientation(SwingConstants.VERTICAL);
		
		quadroProgresso2 = new JProgressBar();
		quadroProgresso2.setOrientation(SwingConstants.VERTICAL);
		quadroProgresso2.setForeground(Color.MAGENTA);
		
		lbl1 = new JLabel("Advers\u00E1rio");
		
		lbl2 = new JLabel("Advers\u00E1rio");
		lblHistorico = new JLabel("Hist�rico");
		GroupLayout gl_panePlacar = new GroupLayout(painelPlacar);
		gl_panePlacar.setHorizontalGroup(
			gl_panePlacar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panePlacar.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panePlacar.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panePlacar.createSequentialGroup()
							.addComponent(lbl1)
							.addGap(18)
							.addComponent(quadroProgresso1, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addComponent(lblHistorico, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
					.addGap(66)
					.addComponent(lbl2)
					.addGap(18)
					.addComponent(quadroProgresso2, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(43, Short.MAX_VALUE))
		);
		gl_panePlacar.setVerticalGroup(
			gl_panePlacar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panePlacar.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panePlacar.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panePlacar.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, gl_panePlacar.createSequentialGroup()
								.addComponent(lbl2)
								.addGap(33))
							.addGroup(Alignment.TRAILING, gl_panePlacar.createSequentialGroup()
								.addGroup(gl_panePlacar.createParallelGroup(Alignment.TRAILING)
									.addComponent(quadroProgresso2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
									.addComponent(quadroProgresso1, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblHistorico, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)))
						.addGroup(Alignment.TRAILING, gl_panePlacar.createSequentialGroup()
							.addComponent(lbl1)
							.addGap(39))))
		);
		painelPlacar.setLayout(gl_panePlacar);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(471, 459);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/***
	 * Método usado para conectar no server socket, retorna IO Exception caso de
	 * algum erro.
	 * 
	 * @throws IOException
	 */
	public void conectar() throws IOException {

		socket = new Socket(textoIp.getText(), Integer.parseInt(textoPorta.getText()));
		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);
		bfw.write(textoNome.getText() + "\r\n");
		bfw.flush();
	}


	/**
	 * Envia para o servidor a mensagem digitada
	 * @param msg
	 * @throws IOException
	 */	
	public void enviarMensagem(String msg) throws IOException {

		if (msg.equals("Sair")) {
			bfw.write("Desconectado \r\n");
			texto.append("Desconectado \r\n");
		} else {
			bfw.write(msg + "\r\n");
			texto.append(textoNome.getText() + " diz -> " + textoMensagem.getText() + "\r\n");
		}
		bfw.flush();
		textoMensagem.setText("");
	}

	/**
	 * Esculta o servidor e trata as mensagens que chegam dele
	 * @throws IOException
	 */	
	public void escutar() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Sair".equalsIgnoreCase(msg))

			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Sair"))
					texto.append("Servidor caiu! \r\n");
				else
					if (msg.length()>1 &&  "#".equalsIgnoreCase(msg.substring(0, 1))){
						setPanel(msg.substring(1, msg.length()));
					}
					else if (msg.length()>1 &&  "*".equalsIgnoreCase(msg.substring(0, 1))){
						setPonts(msg.substring(1, msg.length()));
					}else
					   texto.append(msg + "\r\n");
			}
	}

	/**
	 * 
	 * @param s
	 */
	public void setPonts(String s){

		int i = Integer.parseInt(s);

		if (i==1){
		   quadroProgresso1.setValue(quadroProgresso1.getValue()+20);
		}
		else if(i==2)
			quadroProgresso2.setValue(quadroProgresso2.getValue()+20);
		
	}
	public void setPanel(String s){
		if(s.equals("1")){
			lbl1.setText("Voc�");
		}
		else if(s.equals("2")){
			lbl2.setText("Voc�");
		}
			
	}
	

	/**
	 * Finaliza a conexao e fecha objetos necessários
	 * @throws IOException
	 */
	public void sair() throws IOException {

		enviarMensagem("Sair");
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals(btnSend.getActionCommand()))
				enviarMensagem(textoMensagem.getText());
			else if (e.getActionCommand().equals(btnSair.getActionCommand()))
				sair();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	/**
	 * Ao pressionar 'enter' pega-se o texto e envia a mensagem
	 */
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensagem(textoMensagem.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) throws IOException {

		Cliente app = new Cliente();
		app.conectar();
		app.escutar();
	}
	
	public JProgressBar getProgressBar() {
		return quadroProgresso1;
	}
	public JLabel getLbl1() {
		return lbl1;
	}
	public JLabel getLbl2() {
		return lbl2;
	}
}
