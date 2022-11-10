package Chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.*;
import java.io.*;

public class Server1To1 extends JFrame {	//1:1채팅 서버를 위한 클래스
	protected JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	protected JTextField inputField = new JTextField();	//전송을 위한 영역
	protected JButton sendBtn = new JButton("전송");

	protected ServerSocket server = null;	//서버를 열기위한 서버소켓
	protected Socket client = null;	//클라이언트와 채팅을 하기위한 클라이언트소켓
	protected BufferedReader reader = null;	//클라이언트의 문자를 받아올 스트림
	protected BufferedWriter writer = null; 	//클라이언트에 문자를 보낼 스트림 
	
	protected JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	protected String name;
	
	public Server1To1(String name, String title) {	//단순채팅을 위한 생성자
		setTitle(title);
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
	
	public Server1To1(String title) {	//끝말잇기 채팅을 위한 생성자
		setTitle(title);
		setLayout(new BorderLayout());
		
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
	
	class sendActionListener implements ActionListener {	//단순채팅 액션이벤트 (버튼과 엔터둘다 이용해 이벤트 처리를 하기위해 따로생성)
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendMessage = inputField.getText();	//전송할 내용을 받을 문자열
			try {
				writer.write(name + "\n");	//반대측에 사용할 닉네임을 넘겨줌
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
				writer.write("상대측에서 검색할 수 없는 단어를 입력하였습니다.\n");	//\n이 없으면 바로 넘어가지 않고 창이 닫혀야 넘어감
				writer.flush();
				outputArea.append("사전에서 검색할 수 없는 단어입니다.\n");
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
			JSONObject sense = (JSONObject)itemArray.get("sense");
			
			if(word.length() < 2) {
				writer.write("상대측에서 2글자 미만의 단어를 입력하였습니다.\n");
				writer.flush();
				outputArea.append("단어는 2글자이상 입력해주세요.\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
			}
			else {
				writer.write(itemArray.get("word") + " (사전적 의미) " + sense.get("definition") + "\n");
				writer.flush();
				outputArea.append("[나] : " + itemArray.get("word") + " (사전적 의미) " + sense.get("definition") + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				inputField.setText("");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
