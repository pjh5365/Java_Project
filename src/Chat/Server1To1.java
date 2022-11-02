package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class Server1To1 extends JDialog implements Runnable {	//메인메소드에서만 실행하면 스레드가 필요없는데 따로 이동한다음 실행할려면 스레드가 필요한듯
	private JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	private JTextField inputField = new JTextField();	//전송을 위한 영역
	private JButton sendBtn = new JButton("전송");

	private ServerSocket server = null;	//서버를 열기위한 서버소켓
	private Socket client = null;	//클라이언트와 채팅을 하기위한 클라이언트소켓
	private BufferedReader reader = null;	//클라이언트의 문자를 받아올 스트림
	private BufferedWriter writer = null; 	//클라이언트에 문자를 보낼 스트림 
	
	private JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	private String name;
	
	public Server1To1(String name) {
		setTitle("1:1 채팅서버");
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
	
	public void run() {
		try {
			server = new ServerSocket(9000);
			client = server.accept();
			outputArea.setText("[" + client.getInetAddress() + "] 와 연결 성공 \n\n");
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//반대편에서 문자를 받아올 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//반대편으로 문자를 보낼 스트림
			while(true) {
				String readMessage = reader.readLine();
				
				if(readMessage == null) {
					outputArea.setText(outputArea.getText() + "\n상대측 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + "[" + client.getInetAddress() + "] : " + readMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
			}
		} catch (IOException e) {
			outputArea.setText("연결할 수 없음 \n");
			outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
			System.out.println(e.getMessage());
		}
		finally {
			try {
				server.close();
				client.close();
			} catch(IOException e) {
				outputArea.setText(outputArea.getText() + "\n 종료 중 오류가 발생했습니다. \n");
			}
		}
	}
	
	class sendActionListener implements ActionListener {	//버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			try {
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
