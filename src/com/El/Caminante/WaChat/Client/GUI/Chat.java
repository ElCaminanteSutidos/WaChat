package com.El.Caminante.WaChat.Client.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.El.Caminante.WaChat.Client.Client;

public class Chat extends JFrame {

	private JPanel contentPane;
	public JTextField txtChattext;
	private JScrollPane scrollPane;
	private JTextArea txtrChatarea;
	Client client;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// Chat frame = new Chat();
	// frame.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	/**
	 * Create the frame.
	 */
	public Chat(Client client) {
		this.client = client;
		setTitle("WaChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 403, 312);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		txtrChatarea = new JTextArea();
		txtrChatarea.setWrapStyleWord(true);
		txtrChatarea.setEditable(false);
		txtrChatarea.setLineWrap(true);
		scrollPane.setViewportView(txtrChatarea);

		txtChattext = new JTextField();
		txtChattext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					client.SendChatMSG(txtChattext.getText());
					txtChattext.setText("");
				}
			}
		});
		GridBagConstraints gbc_txtChattext = new GridBagConstraints();
		gbc_txtChattext.insets = new Insets(0, 0, 0, 5);
		gbc_txtChattext.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtChattext.gridx = 0;
		gbc_txtChattext.gridy = 1;
		contentPane.add(txtChattext, gbc_txtChattext);
		txtChattext.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.SendChatMSG(txtChattext.getText());
				txtChattext.setText("");
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 1;
		contentPane.add(btnSend, gbc_btnSend);
	}

	public void println(String line) {
		txtrChatarea.append(line + "\n");
		txtrChatarea.moveCaretPosition(txtrChatarea.getText().length() - 1);
	}

}
