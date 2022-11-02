package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Chat1To1 extends JDialog implements Runnable {	//메인메소드에서만 실행하면 스레드가 필요없는데 따로 이동한다음 실행할려면 스레드가 필요한듯
	private JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	private JTextField inputField = new JTextField();	//전송을 위한 영역
	private JButton sendBtn = new JButton("전송");
	
	private BufferedReader reader = null;	//서버에서 받은 문자를 받기위한 스트림
	private BufferedWriter writer = null;	//서버로 문자를 보낼 스트림
	private Socket client = null;	//서버와 통신을 위한 소켓
	
	private JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	
	private String IPAddress;	//ip주소를 입력받기위한 문자열
	private String nickname;	//닉네임을 넘겨받기위한 문장려
	
	public Chat1To1(String IPAddress, String nickname) {
		setTitle("1:1 채팅");
		setLayout(new BorderLayout());
		
		this.IPAddress = IPAddress;	//IP주소를 입력받아 서버에 연결하기 위해
		this.nickname = nickname;	//닉네임을 받아 표시하기 위해
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListener sAL = new sendActionListener();
		
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
	
	public void run() {
		try {
			client = new Socket(IPAddress, 9000);	//입력받은 IP주소와 9000번 포트로 서버연결
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//클라이언트에서 들어오는 문자를 받을 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//서버로 보낼 문자를 담을 스트림
			outputArea.setText("[" + IPAddress + "] 에 연결 완료\n\n");
			
			while(true) {
				String readMessage = reader.readLine();
				
				if(readMessage == null)	{	//상대쪽 연결이 끊겨서 null을 읽을때 종료
					outputArea.setText(outputArea.getText() + "\n상대쪽 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + "[" + IPAddress + "] : " + readMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
			}
		} catch(IOException e) {
			outputArea.setText("연결할 수 없음 \n");
			outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
		}
		finally {
			try {
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
				outputArea.append("[" + nickname + "] : " + sendMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
				} catch(IOException e1) {
					System.out.println("전송중오류발생");
				}
			
		}
	}
}
