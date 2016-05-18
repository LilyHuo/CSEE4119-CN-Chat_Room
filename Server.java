import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
	
    public static void main(String[] args) throws Exception{
		try {
			// listening on the entered port  
			final ServerSocket listener = new ServerSocket(Integer.valueOf(args[0]));
			// realize the control+c logout part on the server side
			Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
			    try {
			        listener.close();
			        System.out.println("~~The server is shut down!~~");
			    } catch (IOException e) {
			    	System.out.println("~~There is something wrong with the listener~~");}}});
			
			System.out.println("Server is running now!");
			int count = 1;

			while(true){
				// accept the connection made to the listener
				Socket temp =listener.accept();
				System.out.println("The "+ count + "-th user");
				// create a dedicated User object for each connection
			    User cur = new User(temp);
				cur.start();
				System.out.println("The "+ count + "-th user is running!");
				count++;
			}
			
		} catch (IOException e) {
			System.out.println("listener is not active.");
		}
	}
}


