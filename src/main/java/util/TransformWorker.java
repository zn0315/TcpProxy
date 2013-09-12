package util;

import java.io.*;

public class TransformWorker extends Thread {
	InputStream in;
	OutputStream out;
	int delay;
	public TransformWorker(InputStream in, OutputStream out, int delay) {
		this.in = in;
		this.out = out;
		this.delay = delay;
	}
	
	public void run() {
		while (true) {
			try {
				int i = in.read();
				Thread.sleep(delay);
				out.write(i);
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
