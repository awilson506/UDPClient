package threads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class PrintPage implements Runnable {
	
	byte[] b;
	public PrintPage(byte[] b){
		this.b = b;
	}
	
	
	public void run(){
		
		FileOutputStream fop = null;
		File file;
		file = new File("output.txt");
		
		
			  try {
			    boolean written = false;
			    do {
			      try {
			    	  fop = new FileOutputStream(file.getAbsoluteFile(), true);
			        // Lock it!
			        FileLock lock = fop.getChannel().lock();
			        try {
			          // Write the bytes.
			          fop.write(b);
			          written = true;
			        } finally {
			          // Release the lock.
			          lock.release();
			        }
			      } catch ( OverlappingFileLockException ofle ) {
			        try {
			          // Wait a bit
			          Thread.sleep(0);
			        } catch (InterruptedException ex) {
			          throw new InterruptedIOException ("Interrupted waiting for a file lock.");
			        }
			      }
			    } while (!written);
			  } catch (IOException ex) {
			    System.out.print("Lock failed");
			  }
		
	}
}
