package util;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpMutiSessionProxy extends TcpProxy {
	private static Logger logger = LoggerFactory.getLogger(TcpMutiSessionProxy.class);
	
	Selector selector;
	Map<SocketChannel, TcpSession> sessions;

	public TcpMutiSessionProxy(int srcPort, String dstIp, int dstPort, int delay) {
		super(srcPort, dstIp, dstPort, delay);
		sessions = new HashMap<SocketChannel, TcpSession>();
	}
	
	public void start() throws IOException {

		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ServerSocket listenSock = ssc.socket();
		listenSock.bind(new InetSocketAddress(srcPort));
		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		while (true) {
			selector.select();
			Iterator iter = selector.selectedKeys().iterator();
			while (iter.hasNext()) {
				process((SelectionKey)iter.next());
				iter.remove();
			}
		}
	}
	
	private void process(SelectionKey key) throws UnknownHostException, IOException {
		int op = key.readyOps();
		SelectableChannel channel = key.channel();
		logger.trace("event is " + key + " channel is " + channel);
		switch (op) {
		case SelectionKey.OP_ACCEPT:
			SocketChannel dstSC = SocketChannel.open();
			dstSC.connect(new InetSocketAddress(InetAddress.getByName(dstIp),dstPort));
			dstSC.configureBlocking(false);
			dstSC.register(selector, SelectionKey.OP_READ);
			SocketChannel srcSC = ((ServerSocketChannel)channel).accept();
			srcSC.configureBlocking(false);
			srcSC.register(selector, SelectionKey.OP_READ);
			TcpSession newSession = new TcpSession(srcSC, dstSC, delay);
			sessions.put(srcSC, newSession);
			sessions.put(dstSC, newSession);
			break;
		case SelectionKey.OP_READ:
			if (sessions.containsKey(channel)) {
				TcpSession session = sessions.get(channel);
				try {
					session.process((SocketChannel)channel);
				} catch (Exception e) {
					sessions.remove(session.getCliSC());
					sessions.remove(session.getSvrSC());
				}
			} else {
				logger.error("can't find session for:", channel);
			}
			break;
		}
		
	}

}
