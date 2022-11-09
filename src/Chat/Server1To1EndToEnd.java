package Chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;

public class Server1To1EndToEnd extends Server1To1 implements Runnable {
	public Server1To1EndToEnd(String title) {
		super(title);
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(9000);
			client = server.accept();
			outputArea.setText("[상대] 와 연결 성공 \n");
			outputArea.append("표준국어대사전 API를 사용하기 때문에 국어대사전에 없다면 검색되지 않습니다.\n");
			outputArea.append("API를 불러오기때문에 약간의 대기시간이 필요합니다.\n\n");
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//반대편에서 문자를 받아올 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//반대편으로 문자를 보낼 스트림
			while(true) {
				String readMessage = reader.readLine();
				
				if(readMessage == null) {
					outputArea.setText(outputArea.getText() + "\n상대측 연결 끊김 \n");
					break;
				}
				outputArea.setText(outputArea.getText() + "[상대] : " + readMessage + "\n");
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
