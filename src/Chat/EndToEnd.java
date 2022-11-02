package Chat;

import java.awt.*;
import javax.swing.*;

public class EndToEnd extends JDialog{
	private JTextArea outputArea = new JTextArea();	//내용이 들어갈 영역
	private JTextField inputField = new JTextField();	//전송을 위한 영역
	private JButton sendBtn = new JButton("전송");
	
	private JScrollPane outputText;	//스크롤 고정을 위해 바깥에서 선언
	
	public EndToEnd() {
		setTitle("끝말잇기");
		setLayout(new BorderLayout());
		
		outputArea.setEditable(false);	//출력만 하므로 수정불가능하게 만들기
		outputText = new JScrollPane(outputArea);
		outputText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);	//스크롤 고정
		
		JPanel sendText = new JPanel();
		
		sendText.setLayout(new BorderLayout());
		
		sendText.add(inputField, BorderLayout.CENTER);
		sendText.add(sendBtn, BorderLayout.EAST);
		
		Container c = getContentPane();
		c.add(outputText, BorderLayout.CENTER);
		c.add(sendText, BorderLayout.SOUTH);
		
		setSize(400, 500);
	}

}