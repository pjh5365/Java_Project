package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame{
	private	JButton chat1To1;	//1대1 채팅버튼
	private JButton chat1ToN;	//그룹 채팅버튼
	private JButton endToEnd;	//끝말잇기 버튼
	
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
				SelectChat1To1 chat1 = new SelectChat1To1(nickname);
				chat1.setVisible(true);
			}
		});
		
		chat1ToN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Chat1ToN chat2 = new Chat1ToN();	//그룹채팅이 실행되는 부분
				chat2.setVisible(true);
			}
		});
		
		endToEnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EndToEnd endtoend = new EndToEnd();	//끝말잇기가 실행되는 부분
				endtoend.setVisible(true);
			}
		});
		
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
	
	public static void main(String[] args) {
		new MainMenu().login();
	}
}
