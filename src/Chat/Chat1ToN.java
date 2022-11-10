package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chat1ToN extends JFrame {
	private JButton openServer;
	private JButton joinServer;
	
	private String IPAddress;
	private String nickname;
	
	public Chat1ToN(String nickname, String title) {	//그룹 채팅 생성자
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
				Server1ToN ServerThread = new Server1ToN(title);
				Thread thread1 = new Thread(ServerThread);
				thread1.start();
			}
		});
	
		joinServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nickname = JOptionPane.showInputDialog("채팅방에서 사용할 이름을 입력하세요.");
				IPAddress = JOptionPane.showInputDialog("접속할 서버의 IP를 입력하세요.");
				Client1ToN ClientThread = new Client1ToN(IPAddress, nickname, title);
				Thread thread2 = new Thread(ClientThread);
				thread2.start();
			}
		});
		
		c.add(openServer);
		c.add(joinServer);
		
		setSize(400, 500);
	}
}