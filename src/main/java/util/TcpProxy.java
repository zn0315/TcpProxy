package util;

import java.io.*;
import java.net.*;

public class TcpProxy {
	protected int srcPort;
	protected String dstIp;
	protected int dstPort;
	protected int delay;
		
	public TcpProxy(int srcPort, String dstIp, int dstPort, int delay) {
		this.srcPort = srcPort;
		this.dstIp = dstIp;
		this.dstPort = dstPort;
		this.delay = delay;
	}
	
	public void start() throws IOException{
		ServerSocket listenSock;
		listenSock = new ServerSocket(srcPort);
		Socket cliSock = listenSock.accept();
		Socket svrSock = new Socket(InetAddress.getByName(dstIp),dstPort);
		InputStream cliIn = cliSock.getInputStream();
		OutputStream cliOut = cliSock.getOutputStream();
		InputStream svrIn = svrSock.getInputStream();
		OutputStream svrOut = svrSock.getOutputStream();
		TransformWorker w1 = new TransformWorker(cliIn,svrOut,delay);
		TransformWorker w2 = new TransformWorker(svrIn, cliOut, delay);
		w1.start();
		w2.start();
		try {
			while (w1.isAlive() && w2.isAlive())
				Thread.sleep(100);
		} catch (Exception e) {}
		cliSock.close();
		svrSock.close();
	}


}
