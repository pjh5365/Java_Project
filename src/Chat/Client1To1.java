package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

public class Client1To1 extends JFrame {	//1:1채팅을 위한 클라이언트 클래스
	protected JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	protected JTextField inputField = new JTextField();	//전송을 위한 영역
	protected JButton sendBtn = new JButton("전송");
	
	protected BufferedReader reader = null;	//서버에서 받은 문자를 받기위한 스트림
	protected BufferedWriter writer = null;	//서버로 문자를 보낼 스트림
	protected Socket client = null;	//서버와 통신을 위한 소켓
	
	protected JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	
	protected String IPAddress;	//ip주소를 입력받기위한 문자열
	protected String nickname;	//닉네임을 넘겨받기위한 문장려
	
	public Client1To1(String IPAddress, String nickname, String title) {	//단순채팅 생성자 IP주소와 닉네임과 타이틀을 넘겨받음
		setTitle(title);
		setLayout(new BorderLayout());
		
		this.IPAddress = IPAddress;	//IP주소를 입력받아 서버에 연결하기 위해
		this.nickname = nickname;	//닉네임을 받아 표시하기 위해
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListenerChat sAL = new sendActionListenerChat();
		
		inputField.addActionListener(sAL);	//엔터를 입력받을떄 실행
		sendBtn.addActionListener(sAL);	//버튼을 클릭했을때 실행
		
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
	
	public Client1To1(String IPAddress, String nickname, String title, int i) {	//그룹채팅 생성자 IP주소와 닉네임과 타이틀을 넘겨받음
		setTitle(title);
		setLayout(new BorderLayout());
		
		this.IPAddress = IPAddress;	//IP주소를 입력받아 서버에 연결하기 위해
		this.nickname = nickname;	//닉네임을 받아 표시하기 위해
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListenerGroupChat sALG = new sendActionListenerGroupChat();
		
		inputField.addActionListener(sALG);	//엔터를 입력받을떄 실행
		sendBtn.addActionListener(sALG);	//버튼을 클릭했을때 실행
		
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
	
	
	class sendActionListenerChat implements ActionListener {	//단순채팅 액션이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			try {
				writer.write(nickname + "\n");	//반대측에 사용할 닉네임을 넘겨줌
				writer.write(sendMessage + "\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
				outputArea.append("[" + nickname + "] : " + sendMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
				} catch(IOException e1) {
					System.out.println("전송중오류발생");
				}
			
		}
	}
	
	class sendActionListenerGroupChat implements ActionListener {	//그룹채팅 액션이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			try {
				writer.write(nickname + "\n");	//반대측에 사용할 닉네임을 넘겨줌
				writer.write(sendMessage + "\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
//				outputArea.append("[" + nickname + "] : " + sendMessage + "\n");
//				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
				} catch(IOException e1) {
					System.out.println("전송중오류발생");
				}
			
		}
	}
}
