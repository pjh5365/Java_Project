package Chat;

import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client1To1EndToEnd extends Client1To1 implements Runnable {	//서버랑 클라이언트랑 같은 프로그램에서 여는 경우도 있기때문에 쓰레드가 필요함
	private String readMessage;
	
	public Client1To1EndToEnd(String IPAddress, String title) {
		super(IPAddress, title);
		
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
			client = new Socket(IPAddress, 9001);	//입력받은 IP주소와 9001번 포트로 서버연결	1:1단순채팅과 다른서버를 위해 포트번호가 바뀜
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//클라이언트에서 들어오는 문자를 받을 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//서버로 보낼 문자를 담을 스트림
			outputArea.setText("[상대] 와 연결 성공 \n");
			outputArea.append("표준국어대사전 API를 사용하기 때문에 국어대사전에 없다면 검색되지 않습니다.\n");
			outputArea.append("API를 불러오기때문에 약간의 대기시간이 필요합니다.\n\n");
			
			while(true) {
				readMessage = reader.readLine();
				
				if(readMessage == null)	{	//상대쪽 연결이 끊겨서 null을 읽을때 종료
					outputArea.setText(outputArea.getText() + "\n상대쪽 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + "[상대] : " + readMessage + "\n");
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
}
