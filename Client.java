import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	
	public static void main(String[] args) throws IOException {
		// create a socket with entered parameters
		final Socket socket = new Socket(args[0],Integer.valueOf(args[1]));
		// read-in from server-side
	    	BufferedReader input = new BufferedReader(
                	new InputStreamReader(socket.getInputStream()));
	    	// write-out with auto-flush
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		// read-in from user-side
		BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
		
		// realize the control+c logout part on the server side
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
			    try {
			        socket.close();
			    } catch (IOException e) {
			    	System.out.println("~~There is something wrong with the client socket~~");}}});

		System.out.println("Client execute");
		//listening on both server and user side
		
		while(socket.isConnected()&&(!socket.isClosed())){
			if(input.ready()){
				String msg = input.readLine();
				System.out.println(msg);
			}
			if(user_input.ready()){
				String s = user_input.readLine();
				output.println(s);
			}
		}
	}

}
