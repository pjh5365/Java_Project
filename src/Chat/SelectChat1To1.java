package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectChat1To1 extends JDialog {
	private JButton openServer;
	private JButton joinServer;
	
	private String IPAddress;
	private String nickname;
	
	public SelectChat1To1(String nickname) {
		setTitle("1:1 채팅");
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
				Server1To1 ServerThread = new Server1To1(name);
				Thread thread1 = new Thread(ServerThread);
				thread1.start();
			}
		});
		
		joinServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IPAddress = JOptionPane.showInputDialog("접속할 서버의 IP를 입력하세요.");
				Chat1To1 ClientThread = new Chat1To1(IPAddress, nickname);
				Thread thread2 = new Thread(ClientThread);
				thread2.start();
			}
		});
		
		c.add(openServer);
		c.add(joinServer);
		
		setSize(400, 500);
	}
	
}
