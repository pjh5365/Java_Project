package Chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client1To1EndToEnd extends Client1To1 implements Runnable {	//메인메소드에서만 실행하면 스레드가 필요없는데 따로 이동한다음 실행할려면 스레드가 필요한듯
	public Client1To1EndToEnd(String IPAddress, String title) {
		super(IPAddress, title);
	}
	
	@Override
	public void run() {
		try {
			client = new Socket(IPAddress, 9000);	//입력받은 IP주소와 9000번 포트로 서버연결
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//클라이언트에서 들어오는 문자를 받을 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//서버로 보낼 문자를 담을 스트림
			outputArea.setText("[상대] 와 연결 성공 \n");
			outputArea.append("표준국어대사전 API를 사용하기 때문에 국어대사전에 없다면 검색되지 않습니다.\n");
			outputArea.append("API를 불러오기때문에 약간의 대기시간이 필요합니다.\n\n");
			
			while(true) {
				String readMessage = reader.readLine();
				
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
