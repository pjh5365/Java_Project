package Chat;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Server1ToN extends JFrame implements Runnable {
	protected JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역

	protected JScrollPane outputText;	//자동스크롤을 사용하기위해 바깥쪽에 생성
	protected ServerSocket server = null;	//서버를 열기위한 서버소켓
	protected Socket client = null;	//클라이언트와 채팅을 하기위한 클라이언트소켓
	private BufferedWriter writer = null;	//클라이언트로 보낼 문자열을 저장할 스트림
	
	private ArrayList <Socket> clientList = new ArrayList<Socket>();
	private ArrayList <BufferedWriter> writerList = new ArrayList<BufferedWriter>();
	private HashMap <Socket, BufferedWriter> hashmap = new HashMap <Socket, BufferedWriter>();	//쓰레드에서 돌아가는 값을 알아내기 위해 사용

	public Server1ToN(String title) {
		setTitle(title + " 서버");	//클라이언트 창과 구분하기 위해 서버추가
		setLayout(new BorderLayout());
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);	//채팅내역을 보여줄 스크롤팬
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//옆쪽에 항상 스크롤바가 보임
		
		Container c = getContentPane();
		c.add(outputText, BorderLayout.CENTER);
		
		setSize(400, 300);	//사이즈 일반 클라이언트와 같을 필요 없으니 줄임
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {	//창닫았을때 반대쪽에 종료했다는것을 알리기 위해
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("클라이언트 닫음");
				try {
					server.close();
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
			server = new ServerSocket(9002);	//그룹채팅서버 9002포트로 열기
			while(true) {
				client = server.accept();
				clientList.add(client);
				writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));	//쓰레드마다 writer생성
				writerList.add(writer);
				hashmap.put(client, writer);
				
				ServerThread serverThread = new ServerThread(outputText, outputArea, client, clientList, writerList, hashmap);
				Thread Thread1 = new Thread(serverThread);
				Thread1.start();
			}
		} catch(IOException e) {
			System.out.println("Server1ToN.java 속 IOE예외");
		} finally {
			try {
				for(int i = 0; i < clientList.size(); i++)
					clientList.get(i).close();
				server.close();
			} catch(IOException e1) {
				System.out.println("Server1ToN.java 속 서버 종료 예외");
			}
		}
	}
}
