package SkyPlay2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
	Socket s = null;
	
	public TestClient() throws UnknownHostException, IOException {
		s = new Socket("127.0.0.1", 30281);
		System.out.println("Socket!");
		ObjectOutputStream mObjectOutputStream = new ObjectOutputStream(s.getOutputStream());
		System.out.println("mObjectOutputStream!");
		ObjectInputStream mObjectInputStream = new ObjectInputStream(s.getInputStream());
		System.out.println("mObjectInputStream!");
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		new TestClient();
	}
}
