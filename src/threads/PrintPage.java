package threads;



public class PrintPage extends Thread {
	
	byte[] b;
	private String fileName;
	public PrintPage(byte[] b, String fileName){
		this.b = b;
		this.fileName = fileName;
	}
	
	
	public void run(){
		Singleton.getInstance().writeToFile(b, fileName);
		
	}
}
