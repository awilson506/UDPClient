package threads;



public class PrintPage extends Thread {
	
	byte[] b;
	public PrintPage(byte[] b){
		this.b = b;
	}
	
	
	public void run(){
		Singleton.getInstance().writeToFile(b);
		
	}
}
