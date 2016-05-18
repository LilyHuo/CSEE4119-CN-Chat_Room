import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ValidUser {
	private HashMap<String,String> mapping = new HashMap<String, String>();
	
	public ValidUser(){
		//read user_pass.txt
		File file = new File("user_pass.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {System.out.println("Read user_pass.txt error "+ e);}
		try {
			while(reader.ready()){
				String tuple = reader.readLine();
				int index = tuple.indexOf(" ");
				String name = tuple.substring(0, index);
				String psw = SHA1_encoder(tuple.substring(index+1, tuple.length()));
				System.out.println("name : "+name+"  pswd : "+ psw);
				mapping.put(name, psw);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//validation
	public boolean isValidUser(String name, String psw){
		if(mapping.containsKey(name)){
			String real_psw = mapping.get(name);
			if(real_psw.equals(psw)) return true;
		}
		return false;
	}
	
	// encoding
	public static String SHA1_encoder(String s) throws NoSuchAlgorithmException{
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(s.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
	
	/* test
	public static void main(String[] args) throws Exception {
		ValidUser validator = new ValidUser();
	}*/
}
