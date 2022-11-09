package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chat1To1 extends JFrame {	//1:1 채팅을 위한 클래스
	private JButton openServer;
	private JButton joinServer;
	
	private String IPAddress;
	private String nickname;
	
	public Chat1To1(String nickname, String title) {	//1:1 채팅 생성자
		setTitle(title);
		Container c = getContentPane();
		c.setLayout(null);
		
		this.nickname = nickname;
		
		openServer = new JButton("서버 열기");
		joinServer = new JButton("서버 입장");
		
		openServer.setSize(200, 100);
		openServer.setLocation(100, 100);
		joinServer.setSize(200, 100);
		joinServer.setLocation(100, 200);
		
		openServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("사용할 이름을 입력해주세요.");
				Server1To1Chat ServerThread = new Server1To1Chat(name, title);
				Thread thread1 = new Thread(ServerThread);
				thread1.start();
			}
		});
	
		joinServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IPAddress = JOptionPane.showInputDialog("접속할 서버의 IP를 입력하세요.");
				Client1To1Chat ClientThread = new Client1To1Chat(IPAddress, nickname, title);
				Thread thread2 = new Thread(ClientThread);
				thread2.start();
			}
		});
		
		c.add(openServer);
		c.add(joinServer);
		
		setSize(400, 500);
	}
	
	public Chat1To1(String title) {	//끝말잇기 생성자
		setTitle(title);
		Container c = getContentPane();
		c.setLayout(null);
		
		openServer = new JButton("서버 열기");
		joinServer = new JButton("서버 입장");
		
		openServer.setSize(200, 100);
		openServer.setLocation(100, 100);
		joinServer.setSize(200, 100);
		joinServer.setLocation(100, 200);
		
		openServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Server1To1EndToEnd ServerThread = new Server1To1EndToEnd(title);
				Thread thread1 = new Thread(ServerThread);
				thread1.start();
			}
		});
		
		joinServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IPAddress = JOptionPane.showInputDialog("접속할 서버의 IP를 입력하세요.");
				Client1To1EndToEnd ClientThread = new Client1To1EndToEnd(IPAddress, title);
				Thread thread2 = new Thread(ClientThread);
				thread2.start();
			}
		});
		
		c.add(openServer);
		c.add(joinServer);
		
		setSize(400, 500);
	}
}
