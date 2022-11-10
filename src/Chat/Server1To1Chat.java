package Chat;

import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;

public class Server1To1Chat extends Server1To1 implements Runnable {	//1:1채팅을 위한 클래스
	private String nickname;
	private String readMessage;
	
	public Server1To1Chat(String name, String title) {
		super(name, title);
		
		this.addWindowListener(new WindowAdapter() {	//창닫았을때 반대쪽에 종료했다는것을 알리기 위해
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("클라이언트 닫음");
				try {
					client.close();
				} catch(IOException e1) {
					outputArea.setText(outputArea.getText() + "\n 종료 중 오류가 발생했습니다. \n");
				} catch(Exception e2) {	//서로 연결이 안되어있을때를 대비
					System.out.println("서로 연결이 되어있지 않음");
				}
			}
		});
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(9000);
			client = server.accept();
			outputArea.setText("[" + client.getInetAddress() + "] 와 연결 성공 \n\n");
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//반대편에서 문자를 받아올 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//반대편으로 문자를 보낼 스트림
			while(true) {
				nickname = reader.readLine();	//상대측에서 넘겨준 닉네임을 받음 상대측에서 버튼누를때마다 닉네임과 같이 보내니까 while문에 같이 있어야
				readMessage = reader.readLine();
				
				if(readMessage == null) {
					outputArea.setText(outputArea.getText() + "\n상대측 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + "[" + nickname + "] : " + readMessage + "\n");
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
}
