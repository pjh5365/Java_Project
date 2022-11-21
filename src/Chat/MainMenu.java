package Chat;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainMenu extends JFrame{	//메인화면이 그려지는 클래
	private	JButton chat1To1;	//1대1 채팅버튼
	private JButton chat1ToN;	//그룹 채팅버튼
	private JButton endToEnd;	//끝말잇기 버튼
	
	private JLabel covid = new JLabel();	//코로나확진자 수를 위한 JLabel
	private JLabel nickLabel = new JLabel();	//로그인 후 닉네임을 보여주는 레이블
	
	private String nickname;
	
	public MainMenu() {
		setTitle("채팅프로그램");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container c = getContentPane();
		
		chat1To1 = new JButton("1:1 채팅");
		chat1ToN = new JButton("그룹채팅");
		endToEnd= new JButton("끝말잇기");
		
		c.setLayout(null);
		
		covid.setLocation(75, 10);
		covid.setSize(2000, 20);
		
		nickLabel.setLocation(100, 70);
		nickLabel.setSize(200,20);
		chat1To1.setSize(200, 100);
		chat1To1.setLocation(100, 100);
		chat1ToN.setSize(200, 100);
		chat1ToN.setLocation(100, 200);
		endToEnd.setSize(200, 100);
		endToEnd.setLocation(100, 300);
		
		chat1To1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Chat1To1 chat1 = new Chat1To1(nickname, "1:1 채팅");
				chat1.setVisible(true);
			}
		});
		
		chat1ToN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Chat1ToN chat2 = new Chat1ToN(nickname, "그룹 채팅");	//그룹채팅이 실행되는 부분
				chat2.setVisible(true);
			}
		});
		
		endToEnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EndToEnd endtoend = new EndToEnd("끝말잇기");		//끝말잇기가 실행되는 부분
				endtoend.setVisible(true);
			}
		});
		
		callApi();
		
		c.add(covid);	//코로나 확진자 수를 보여주기위한 JLabel
		c.add(nickLabel);
		c.add(chat1To1);
		c.add(chat1ToN);
		c.add(endToEnd);
		setSize(400, 500);
		setVisible(true);
	}
	
	public void login() {	//닉네임을 입력받음
		nickname = JOptionPane.showInputDialog("ID를 입력하세요.");
		nickLabel.setText(nickname + " 으로 로그인 됨");
	}
	
	private void callApi() {	//api를 호출하는 메소드
		String key = "";	//공공데이터 포털의 key값
		String resultApi;	//api의 전체내용을 담을 문자열
		String tmp;	//api의 내용을 임시로 담을 문자열
		
		try {
			URL url = new URL("http://apis.data.go.kr/1790387/covid19CurrentStatusKorea/covid19CurrentStatusKoreaJason?serviceKey=" + key);
			BufferedReader readApi;	//api의 내용을 받을 버퍼리더
			
			readApi = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			tmp = readApi.readLine();
			resultApi = tmp + "\n";
			
			while(true) {	//api를 읽을 수 없을때
				tmp = readApi.readLine();
				if(tmp == null)
					break;
				resultApi = resultApi + tmp + "\n";
			}
			
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject)parser.parse(resultApi);
			JSONObject response = (JSONObject)object.get("response");
			JSONArray result = (JSONArray)response.get("result");
			JSONObject resultArray = (JSONObject)result.get(0);
			String mmddhh = (String)resultArray.get("mmddhh");
			String cnt_confirmations = (String)resultArray.get("cnt_confirmations");
			
			covid.setText(mmddhh + " 기준으로 현재 확진자 수 " + cnt_confirmations + "명 입니다.");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new MainMenu().login();
	}
}
