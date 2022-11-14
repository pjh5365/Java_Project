package Chat;

import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client1ToN extends Client1To1Chat {
	String readMessage;
	String nickname;
	
	public Client1ToN(String IPAddress, String nickname, String title) {
		super(IPAddress, nickname, title, 1);
		this.nickname = nickname;
	}
	
	@Override
	public void run() {
		this.addWindowListener(new WindowAdapter() {	//창닫았을때 반대쪽에 종료했다는것을 알리기 위해
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("클라이언트 닫음");
				try {
					writer.write(nickname + "\n");	//종료할때 닉네임과 널값을 넘기도록 함
					writer.flush();
					client.close();
				} catch(IOException e1) {
					outputArea.setText(outputArea.getText() + "\n 종료 중 오류가 발생했습니다. \n");
				} catch(Exception e2) {	//서로 연결이 안되어있을때를 대비
					System.out.println("서로 연결이 되어있지 않음");
				}
			}
		});
		try {
			client = new Socket(IPAddress, 9002);	//입력받은 IP주소와 9002번 포트로 서버연결
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//클라이언트에서 들어오는 문자를 받을 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//서버로 보낼 문자를 담을 스트림
			outputArea.setText("[서버] 에 연결 완료\n[Enter키]를 눌러 채팅에 참여하세요!\n\n");
			
			while(true) {
				readMessage = reader.readLine();
				
				if(readMessage == null)	{	//상대쪽 연결이 끊겨서 null을 읽을때 종료
					outputArea.setText(outputArea.getText() + "\n서버 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + readMessage + "\n");	//서버측에서 서버에 들어온 문자열을 모든 클라이언트들에게 보내줌
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
