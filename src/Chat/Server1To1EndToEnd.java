package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Server1To1EndToEnd extends JFrame implements Runnable {	//원래 Server1To1을 상속받았지만 규칙추가를 위해 다시작성
	private JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	private JTextField inputField = new JTextField();	//전송을 위한 영역
	private JButton sendBtn = new JButton("전송");

	private ServerSocket server = null;	//서버를 열기위한 서버소켓
	private Socket client = null;	//클라이언트와 채팅을 하기위한 클라이언트소켓
	private BufferedReader reader = null;	//클라이언트의 문자를 받아올 스트림
	private BufferedWriter writer = null; 	//클라이언트에 문자를 보낼 스트림 
	
	private JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	private String readMessage;
	
	public Server1To1EndToEnd(String title) {	//끝말잇기 채팅을 위한 생성자
		setTitle(title);
		setLayout(new BorderLayout());
		
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
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListenerEndToEnd sALE = new sendActionListenerEndToEnd();
		inputField.addActionListener(sALE);
		sendBtn.addActionListener(sALE);
		
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
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(9001);	//1:1단순채팅과 다르게 하기위해 포트번호 9001로 사용
			client = server.accept();
			outputArea.setText("[상대] 와 연결 성공 \n");
			outputArea.append("표준국어대사전 API를 사용하기 때문에 국어대사전에 없다면 검색되지 않습니다.\n기술적 문제때문에 두음법칙이 성립되지 않습니다...\n");	//단순 문자비교이기 때문에 두음법칙 불가...
			outputArea.append("API를 불러오기때문에 약간의 대기시간이 필요합니다. \n항상 상대측의 선공[나무]로 시작됩니다. \n\n");
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//반대편에서 문자를 받아올 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//반대편으로 문자를 보낼 스트림
			while(true) {
				readMessage = reader.readLine();
				
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
	
	class sendActionListenerEndToEnd implements ActionListener {	//끝말잇기채팅 액션이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			callApi(sendMessage);
		}
	}
	
	private void callApi(String word) {
		String key = "";	//표준국어대사전 인증키 입력
		String result;	//api의 전체내용을 담을 문자열
		String tmp;	//api의 내용을 임시로 담을 문자열
		
		try {
			URL url = new URL("http://stdict.korean.go.kr/api/search.do?certkey_no=4492&key=" + key + "&type_search=search&req_type=json&q=" + word);	//에러 https대신 http를 사용하여 해결
			BufferedReader readApi;	//api의 내용을 받을 버퍼리더
			
			readApi = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			tmp = readApi.readLine();
			result = tmp + "\n";
			
			if(tmp == null) {	//api를 읽을 수 없을때
				writer.write("상대측에서 검색할 수 없는 단어 [" + word + "]를 입력하였습니다.\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
				outputArea.append("[" + word + "] 사전에서 검색할 수 없는 단어입니다.\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
			}
			
			while(true) {
				tmp = readApi.readLine();
				if(tmp == null)
					break;
				result = result + tmp + "\n";
			}
			
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject)parser.parse(result);
			JSONObject channel = (JSONObject)object.get("channel");
			JSONArray item = (JSONArray)channel.get("item");
			JSONObject itemArray = (JSONObject)item.get(0);
			
			if(word.length() < 2) {
				writer.write("상대측에서 2글자 미만의 단어 ["+ word +"]를 입력하였습니다.\n");
				writer.flush();
				outputArea.append("단어는 2글자이상 입력해주세요.\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
			}
			else {
				if(readMessage.charAt(readMessage.length()-1) == word.charAt(0)) {	//상대측이 보낸 마지막 문자와 내 첫번쨰 문자 비교
					writer.write(itemArray.get("word") + "\n");
					writer.flush();
					outputArea.append("[나] : " + itemArray.get("word") + "\n");
					outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
					inputField.setText("");
				}
				else {
					writer.write("상대측에서 입력한 단어 [" + word + "] (이)가 마지막 글자와 맞지 않습니다. \n");
					writer.flush();
					outputArea.append("상대의 마지막 글자와 같은 글자를 입력하세요. \n");
					outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
					inputField.setText("");
				}
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
