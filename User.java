import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User extends Thread {
	// the necessary attributes of each connection
	private String name;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private long loginTime;

	// all connections/Users share the fields below
	private  static final ValidUser validator =  new ValidUser();
	private static final HashMap<String,User> cu = new HashMap<String, User>();
	private static final HashSet<String> bl = new HashSet<String>();
	
	// the parameters requireed
	private final int BLOCK_OUT = 60*1000;  //1 minutes = 60*1000 milliseconds
	private final int TIME_OUT = 30*60*1000;//30 minutes = 30*60*1000 milliseconds
	
	//initialization
	public User(Socket socket) throws Exception{
		this.socket = socket;
		this.input = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		this.output = new PrintWriter(socket.getOutputStream(), true);
		this.loginTime = System.currentTimeMillis();
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public long getLoginTime(){
		return this.loginTime;
	}
	
	// authentification procedure for each user
	public boolean Authentification() throws Exception{
		// get ip-address 
		final String ip =socket.getInetAddress().getHostAddress();
		// if this ip-address is in blacklist, namely being blocked
		if(bl.contains(ip)){
			output.println("This ip is in blacklist ! ");	
			socket.close();
			return false;
		}
		else{
			int count = 0;
			while(count<3){
				output.println("Username : ");
				String cur_name = input.readLine();
		// if this is a duplicate login
				if(cu.containsKey(cur_name)) {
					output.println("Do not allow duplicate login ! ");
					count++;
					continue;
				}
				output.println("Password : ");
				String cur_psw = ValidUser.SHA1_encoder(input.readLine());
		// if this user has the right to login
				if(validator.isValidUser(cur_name, cur_psw)){
	    // if all authentication procedure passed
						this.name = cur_name;
						cu.put(this.name, this);
						output.println("Welcome to the simple chat server ! ");	
						break;
				}
				count++;
			}
			
			if(count<3){
				return true;
			}
			else{
		// if input wrong for 3 times
				output.println("There are 3 consecutive failures ! ");	
		// add the ip-address into blacklist and block for 1 minutes
			    bl.add(ip);
			    Timer T=new Timer();
				T.schedule(new TimerTask() {  
					@Override
					public void run() {
						System.out.println("remove one user from blacklist");
						bl.remove(ip);
					}
					}, BLOCK_OUT);
			    socket.close();
			    return false;
			}
		}
	}
	
	public void execution(String msg) throws Exception{
		// see if the command is valid
		if (socket==null) {
			return;
		}
		String pool = "who|last|broadcast|send|logout";
		Pattern r = Pattern.compile(pool);
		Matcher m = r.matcher(msg);
		if(!m.find()){output.println("invalid command "); return;}
		
		String command = m.group(0);
		// the command of "who"		
		if(command.equals("who")){   output.println(cu.keySet().toString());  }
		// the command of "last"		
		else if(command.equals("last")){
			int index = msg.indexOf(" ");
			String content = msg.substring(index+1,msg.length()).trim();
			double interval = Double.valueOf(content);
			Set<String> keyset = cu.keySet();
			HashSet<String> rst = new HashSet<String>();
			long currentTime = System.currentTimeMillis();
			for(String key : keyset){
				User temp = cu.get(key);
				double time = (currentTime - temp.getLoginTime())/1000.0/60;
				if(interval>time) rst.add(temp.name);
			}
				output.println(rst.toString());
		}
		// the command of "broadcast"		
		else if(command.equals("broadcast")){
				int index = msg.indexOf(" ");
				String content = msg.substring(index+1,msg.length());
				Set<String> keyset = cu.keySet();
				for(String key : keyset){
					Socket temp = cu.get(key).getSocket();
					if((temp!=null)&&(!temp.isClosed())){
						PrintWriter bc_temp = new PrintWriter(temp.getOutputStream(),true);
						bc_temp.println(this.name+" : "+content);
					}
				}
			}
		// the command of "send"		
		else if(command.equals("send")){
				int start = msg.indexOf(" ")+1;
				msg = msg.substring(start,msg.length());
				msg = msg.trim();
			    System.out.println(msg);
			    if(msg.charAt(0)=='('){
			    	int end = msg.indexOf(")");
			    	String receivers = msg.substring(1,end);
			    	String[] users = receivers.split(" ");
			    	String message = msg.substring(end+1,msg.length());
					for(String user : users){
					   Socket s =cu.get(user).getSocket();
					   if((s!=null)&&(!s.isClosed())){
						   PrintWriter bc_temp = new PrintWriter(s.getOutputStream(),true);
						   bc_temp.println(this.name+" : "+message);
					   }
					}
			    }
			    else{
			    	int end = msg.indexOf(" ");
					String name = msg.substring(0,end);
					System.out.println("name :" + name);
					String message = msg.substring(end+1,msg.length());
					System.out.println("message :" + message);
					Socket s =cu.get(name).getSocket();
					if((s!=null)&&(!s.isClosed())){
						PrintWriter bc_temp = new PrintWriter(s.getOutputStream(),true);
						bc_temp.println(this.name+" : "+message);
					}
			    }
			}
		
			else{ cu.remove(name);  socket.close();	}
		}
		
	@Override
	public void run() {
		// authentification procedure
		boolean allow = false;
		try { allow = Authentification();} 
		catch (Exception e1) {
			System.out.println("Fail in Authentification Procedure, socket closed.");
		}
		if(allow){
			String msg = null;
			// the inactive interval
			Timer T=new Timer();
			T.schedule(new TimerTask() {  
				@Override
				public void run() {
					try {output.println("time out!");
					socket.close();} 
					catch (IOException e) {System.out.println("socket closed error - "+e);}
					}
				}, TIME_OUT);
			
			while((socket!=null)&&(!socket.isClosed())){
				output.println("Command : ");
				try { 
					msg = input.readLine();
					T.cancel();
					T = new Timer();
					T.schedule(new TimerTask() {  
						@Override
						public void run() {
							try {
								output.println("time out!");
								socket.close();} 
							catch (IOException e) {System.out.println("socket closed error - "+e);}
							}
						}, TIME_OUT);
				}
				catch (Exception e) {System.out.println("Read msg error! - User might logout");}

				if((socket!=null)&&(!socket.isClosed())){
					try { execution(msg);} 
					catch (Exception e) {System.out.println("Execute msg error! - User might logout ");break;}
				}
				else break;
			}
		}
		try { socket.close();} 
		catch (IOException e) {System.out.println("socket closed error - "+e);}
	}
	
}
