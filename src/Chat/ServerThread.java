package Chat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class ServerThread extends JFrame implements Runnable {
	protected JTextArea outputArea;	//텍스트에리어를 받아와 사용하기 위해
	private JScrollPane outputText;	//스크롤팬을 받아와 사용하기 위해

	private ArrayList <Socket> clientList = null;
	
	private BufferedReader reader = null;	//클라이언트에서 문자열을 받아올 스트림
	private BufferedWriter writer = null;	//클라이언트로 보낼 문자열을 저장할 스트림
	
	private String name;	//클라이언트들의 이름을 저장할 문자열
	private String readMessage;
	

	public ServerThread(JScrollPane outputText, JTextArea outputArea, Socket client, ArrayList<Socket> clientList) {
		this.outputText = outputText;
		this.outputArea = outputArea;
		this.clientList = clientList;
		try {
			for(int i = 0; i < clientList.size(); i++) {
				reader = new BufferedReader(new InputStreamReader(clientList.get(i).getInputStream()));
			}
		} catch(IOException e) {
			System.out.println("서버쓰레드 생성자 에러");
		}
	}
	
	@Override
	public void run() {
		try {
			name = reader.readLine();
			outputArea.setText(outputArea.getText() + "[" + name + "] 입장 \n");
			outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
			for(int i = 0; i < clientList.size(); i++) {
				try {
					writer = new BufferedWriter(new OutputStreamWriter(clientList.get(i).getOutputStream()));
					writer.write("\t[" + name + "] 입장 \n");
					writer.flush();
				} catch(IOException e) {
					System.out.println("서버쓰레드 생성자 에러1");
				} 
			}
			reader.readLine();	//클라이언트에서 항상 이름과 내용을 보내므로 이름을 먼저 받고 입장표시를 띄우기 위해 엔터키한번 입력받았을때 채팅에 참여시킴
			
			while(name != null) {
				if(name == null) {
					//상대쪽 연결끊겼을때 끊겼다고 표시하고싶은데 이게 안되네 
				}
				
				name = reader.readLine();
				readMessage = reader.readLine();
				outputArea.setText(outputArea.getText() + "[" + name + "] : " + readMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				
				for(int i = 0; i < clientList.size(); i++) {
					try {
						writer = new BufferedWriter(new OutputStreamWriter(clientList.get(i).getOutputStream()));
						writer.write("[" + name + "] : " + readMessage + "\n");
						writer.flush();
					} catch(IOException e) {
						System.out.println("서버쓰레드 생성자 에러2");
						break;
					}
				}
			}
			
		} catch(IOException e) {
			System.out.println("서버쓰레드 에러");
		} 
	}
}
