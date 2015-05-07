package threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Singleton {
	private static final Singleton inst = new Singleton();

	private Singleton() {
		super();
	}

	public synchronized void writeToFile(byte[] str, String fileName) {

		FileOutputStream fop = null;
		File file;

		try {

			file = new File(fileName);
			fop = new FileOutputStream(file.getAbsoluteFile(), true);
			if (!file.exists()) {
				file.createNewFile();
			}
			try {
				fop.write(str);
			} finally {

			}

			fop.flush();
			fop.close();

		} catch (IOException e) {
			System.out.print("error");
		} finally {

			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Singleton getInstance() {
		return inst;
	}

}