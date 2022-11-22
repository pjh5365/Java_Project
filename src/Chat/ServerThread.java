package Chat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.*;
import javax.swing.*;

public class ServerThread extends JFrame implements Runnable {
	protected JTextArea outputArea;	//텍스트에리어를 받아와 사용하기 위해
	private JScrollPane outputText;	//스크롤팬을 받아와 사용하기 위해
	private Socket client;

	private ArrayList <Socket> clientList = null;
	private ArrayList <BufferedWriter> writerList = null;	//클라이언트 각각에 따로 문자열을 보내주기위해
	private HashMap <Socket, BufferedWriter> hashmap = null;	//쓰레드에서 돌아가는 값을 알아내기 위해 사용 
	
	private BufferedReader reader = null;	//클라이언트에서 문자열을 받아올 스트림 어차피 다들 서버로 보내니까 따로 리스트 할 필요 없을듯 
	private BufferedWriter writer = null;	//클라이언트로 보낼 문자열을 저장할 스트림
	
	private String name;	//클라이언트들의 이름을 저장할 문자열
	private String readMessage;	//클라이언트에서 보낸 문자열을 저장할 문자열
	

	public ServerThread(JScrollPane outputText, JTextArea outputArea, Socket client, ArrayList<Socket> clientList, ArrayList <BufferedWriter> writerList, HashMap <Socket, BufferedWriter> hashmap) {
		this.client = client;
		this.outputText = outputText;
		this.outputArea = outputArea;
		this.clientList = clientList;
		this.writerList = writerList;
		this.hashmap = hashmap;
		try {
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch(IOException e) {
			System.out.println("서버쓰레드 생성자 에러");
		}
	}
	
	@Override
	public void run() {
		try {
			name = reader.readLine();
			readMessage = reader.readLine();	//처음입장시 엔터키 또는 종료시 널값을 가지기 위함	(클라이언트에서 항상 이름과 내용을 보내므로 이름을 먼저 받고 입장표시를 띄우기 위해 엔터키한번 입력받았을때 채팅에 참여시킴)
			outputArea.setText(outputArea.getText() + "\t[" + name + "] 입장 \n");
			outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
			
			for(int i = 0; i < clientList.size(); i++) {
				try {
					writerList.get(i).write("\t[" + name + "] 입장 \n");
					writerList.get(i).flush();
				} catch(IOException e) {
					System.out.println("서버쓰레드 생성자 에러1");
				} 
			}
			
			while(true) {
				
				name = reader.readLine();
				readMessage = reader.readLine();
				
				if(readMessage == null) {
					writer = hashmap.get(client);	//삭제할 현재 writer를 지금 클라이언트키로 해쉬맵에서 받아옴
					hashmap.remove(client);
					writerList.remove(writer);
					clientList.remove(client);
					client.close();
					
					for(int i = 0; i < clientList.size(); i++) {
						writerList.get(i).write("\t[" + name + "] 나감 \n");	//모든 클라이언트에게 종료메시지 출력
						writerList.get(i).flush();
						}
						outputArea.setText(outputArea.getText() + "\t[" + name + "] 나감 \n");
						outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
					break;
				}
				
				outputArea.setText(outputArea.getText() + "[" + name + "] : " + readMessage + "\n");
				outputText.getVerticalScrollBar().setValue(outputText.getVerticalScrollBar().getMaximum());	//자동스크롤
				
				for(int i = 0; i < writerList.size(); i++) {
					try {
						writerList.get(i).write("[" + name + "] : " + readMessage + "\n");
						writerList.get(i).flush();
					} catch(IOException e) {
						System.out.println("서버쓰레드 생성자 에러2");
						break;
					}
				}
			}
			
		} catch(IOException e) {
			System.out.println("서버쓰레드 에러입니다.");
		} 
	}
}
