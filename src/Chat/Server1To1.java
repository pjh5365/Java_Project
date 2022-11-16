package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.net.*;
import java.io.*;

public class Server1To1 extends JFrame {	//1:1채팅 서버를 위한 클래스
	protected JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	protected JTextField inputField = new JTextField();	//전송을 위한 영역
	protected JButton sendBtn = new JButton("전송");

	protected ServerSocket server = null;	//서버를 열기위한 서버소켓
	protected Socket client = null;	//클라이언트와 채팅을 하기위한 클라이언트소켓
	protected BufferedReader reader = null;	//클라이언트의 문자를 받아올 스트림
	protected BufferedWriter writer = null; 	//클라이언트에 문자를 보낼 스트림 
	
	protected JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	protected String name;
	
	public Server1To1(String name, String title) {	//단순채팅을 위한 생성자
		setTitle(title);
		setLayout(new BorderLayout());
		
		this.name = name;
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListener sAL = new sendActionListener();
		inputField.addActionListener(sAL);
		sendBtn.addActionListener(sAL);
		
		JPanel sendText = new JPanel();
		
		sendText.setLayout(new BorderLayout());
		
		sendText.add(inputField, BorderLayout.CENTER);
		sendText.add(sendBtn, BorderLayout.EAST);
		
		Container c = getContentPane();
		c.add(outputText, BorderLayout.CENTER);
		c.add(sendText, BorderLayout.SOUTH);
		
		setSize(400, 500);
		setVisible(true);
	}
		
	class sendActionListener implements ActionListener {	//단순채팅 액션이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			try {
				writer.write(name + "\n");	//반대측에 사용할 닉네임을 넘겨줌
				writer.write(sendMessage + "\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
				outputArea.append("[" + name + "] : " + sendMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
				} catch(IOException e1) {
					System.out.println("전송중오류발생");
				}
			
		}
	}
}
