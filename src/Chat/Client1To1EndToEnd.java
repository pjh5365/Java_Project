package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Client1To1EndToEnd extends JFrame implements Runnable {	//원래 Client1To1을 상속받았지만 규칙추가를 위해 다시작성
	protected JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	protected JTextField inputField = new JTextField();	//전송을 위한 영역
	protected JButton sendBtn = new JButton("전송");
	
	protected BufferedReader reader = null;	//서버에서 받은 문자를 받기위한 스트림
	protected BufferedWriter writer = null;	//서버로 문자를 보낼 스트림
	protected Socket client = null;	//서버와 통신을 위한 소켓
	
	protected JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	
	protected String IPAddress;	//ip주소를 입력받기위한 문자열
	protected String nickname;	//닉네임을 넘겨받기위한 문장려
	
	private String readMessage;
	
	public Client1To1EndToEnd(String IPAddress, String title) {	//끝말잇기 채팅생성자 IP주소와 타이틀을 넘겨받음
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
		
		this.IPAddress = IPAddress;	//IP주소를 입력받아 서버에 연결하기 위해
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		sendActionListenerEndToEnd sALE = new sendActionListenerEndToEnd();
		
		inputField.addActionListener(sALE);	//엔터를 입력받을떄 실행
		sendBtn.addActionListener(sALE);	//버튼을 클릭했을때 실행
		
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
			client = new Socket(IPAddress, 9001);	//입력받은 IP주소와 9001번 포트로 서버연결	1:1단순채팅과 다른서버를 위해 포트번호가 바뀜
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));	//클라이언트에서 들어오는 문자를 받을 스트림
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//서버로 보낼 문자를 담을 스트림
			writer.write("나무\n");		//첫번째 값이 없다면 실행이 되지 않기 때문에 처음값으로 [나무]를 주어 끝말잇기 시작
			writer.flush();
			outputArea.setText("[상대] 와 연결 성공 \n");
			outputArea.append("표준국어대사전 API를 사용하기 때문에 국어대사전에 없다면 검색되지 않습니다.\n기술적 문제때문에 두음법칙이 성립되지 않습니다...\n");	//단순 문자를 비교하기떄문에 두음법칙 불가능...
			outputArea.append("API를 불러오기때문에 약간의 대기시간이 필요합니다.\n[상대]의 입력을 기다리세요. \n\n");
			
			while(true) {
				readMessage = reader.readLine();
				System.out.println(readMessage);
				
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
	
	class sendActionListenerEndToEnd implements ActionListener {	//끝말잇기채팅 이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			callApi(sendMessage);	//api호출 메소드 호출
		}
	}
	
	private void callApi(String word) {	//api를 호출하는 메소드
		String key = "";	//표준국어대사전 인증키 입력
		String result;	//api의 전체내용을 담을 문자열
		String tmp;	//api의 내용을 임시로 담을 문자열
		
		try {
			URL url = new URL("http://stdict.korean.go.kr/api/search.do?certkey_no=4492&key=" + key + "&type_search=search&req_type=json&q=" + word);	//에러 https대신 http를 사용하여 해결
			BufferedReader readApi;	//api의 내용을 받을 버퍼리더
			
			readApi = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			tmp = readApi.readLine();
			result = tmp + "\n";
			
			if(tmp == null) {
				writer.write("상대측에서 검색할 수 없는 단어 [" + word + "]를 입력하였습니다.\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
				outputArea.append("[" + word + "] 사전에서 검색할 수 없는 단어입니다.\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
			}
			
			while(true) {	//api를 읽을 수 없을때
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
				if(readMessage.charAt(readMessage.length()-1) == word.charAt(0)) {	//상대측이 보낸 마지막 문자와 내 첫번째 문자비교
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
