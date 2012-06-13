package SkyPlay2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer extends ServerSocket {
	public TestServer() throws IOException {
		super(30281);
		// TODO Auto-generated constructor stub
		thread.start();
	}
	
	private Thread thread = new Thread() {
		public void run() {
			while(!TestServer.this.isClosed()) {
				try {
					Socket s = TestServer.this.accept();
					System.out.println("accept!");
					ObjectInputStream mObjectInputStream = new ObjectInputStream(s.getInputStream());
					System.out.println("mObjectInputStream!");
					ObjectOutputStream mObjectOutputStream = new ObjectOutputStream(s.getOutputStream());
					System.out.println("mObjectOutputStream!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public static void main(String[] args) throws IOException {
		new TestServer();
	}
}
