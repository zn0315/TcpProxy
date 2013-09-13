package util;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

import org.slf4j.*;

public class TcpSession {
	static int currentId=0;
	static Logger logger = LoggerFactory.getLogger(TcpSession.class);
	private SocketChannel cliSC;
	private SocketChannel svrSC;
	private int delay;
	private ByteBuffer buf;
	private int id;
	public TcpSession(SocketChannel cliSC, SocketChannel svrSC, int delay) {
		id = currentId++;
		logger.debug("session {} is open", id);
		logger.trace( "src channel is {} dst channel is {} delay is {}",cliSC ,svrSC,delay);
		this.cliSC = cliSC;
		this.svrSC = svrSC;
		this.delay = delay;
		buf = ByteBuffer.allocate(1024);
	}
	public void process(SocketChannel channel) throws Exception {
		SocketChannel peer = (channel == cliSC)? svrSC: cliSC;
		buf.clear();
		try {
			int size = channel.read(buf);
			checkChannelOp("read", size,channel);
			logger.trace("session {} read data size:{} from {}",id, size, channel);
			buf.flip();
			Thread.sleep(delay);
			size = peer.write(buf);
			checkChannelOp("write", size,peer);
			logger.trace("session {} write data size:{} to {}",id, size, peer);
		} catch (Exception e) {
			cliSC.close();
			svrSC.close();
			logger.debug("session {} is closed", id);
			throw e;
		}

	}
	
//	public void close() throws IOException {
//		logger.trace("session is closed");
//		cliSC.close();
//		svrSC.close();
//	}
	public SocketChannel getCliSC() {
		return cliSC;
	}
	public SocketChannel getSvrSC() {
		return svrSC;
	}
	
	private void checkChannelOp(String op, int ret, SocketChannel channel) throws ConnectException {
			if (ret == -1) {
				throw(new ConnectException(op + " channel is close:"+channel));
			}
	}
}