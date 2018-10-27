package bgu.spl181.net.srv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private Connections<T> connections;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private AtomicInteger connectionIdCounter;
	
	
    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.connections=new ConnectionsImpl<T>();
		this.connectionIdCounter=new AtomicInteger(0);
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<T> protocol= protocolFactory.get(); 
                
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocol);
                initConnection(protocol,handler);
                execute(handler);
            }
        } 
        catch (IOException ex) {}
        System.out.println("server closed!!!");
    }
    /**
     * @param protocol 
     * @param handler
     * 			adds the handler with the id
     * 			and starts its protocol
     */
    private void initConnection(BidiMessagingProtocol<T> protocol, BlockingConnectionHandler<T> handler) {
    	int nextId= connectionIdCounter.incrementAndGet();
        ((ConnectionsImpl<T>)connections).addConnection(nextId, handler);
        protocol.start(nextId, connections);
	}

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
