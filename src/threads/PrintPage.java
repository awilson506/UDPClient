package threads;



public class PrintPage extends Thread {
	
	byte[] b;
	public PrintPage(byte[] b){
		this.b = b;
	}
	
	
	public void run(){
		Singleton.getInstance().writeToFile(b);
		
		for( int i = 0; i < b.length; i++){
			
			
			System.out.print(new String(b, i,1));
			
	
	}
	}
}
