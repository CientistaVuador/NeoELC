package com.cien.votifier;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Votifier extends Thread {

	private static final Logger LOG = Logger.getLogger("Votifier");
	
	private ServerSocket socket;
	private final int port;
	private final KeyPair pair;
	
	public Votifier(KeyPair pair, int port) throws IOException {
		this.port = port;
		this.pair = pair;
		socket = new ServerSocket(port);
	}

	public KeyPair getPair() {
		return pair;
	}
	
	public int getPort() {
		return port;
	}
	
	private String readString(byte[] data, int offset) {
		StringBuilder builder = new StringBuilder(64);
		for (int i = offset; i < data.length; i++) {
			if (data[i] == '\n') {
				break;
			}
			builder.append((char) data[i]);
		}
		return builder.toString();
	}
	
	@Override
	public void run() {
		while (socket != null) {
			try {
				Socket client = socket.accept();
				client.setSoTimeout(5000);
				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				BufferedInputStream input = new BufferedInputStream(client.getInputStream());
				
				output.write("VOTIFIER 1.0");
				output.newLine();
				output.flush();
				
				byte[] block = new byte[256];
				input.read(block, 0, block.length);
				
				block = KeyManager.decrypt(block, pair);
				int position = 0;
				
				String opcode = readString(block, position);
				position += opcode.length() + 1;
				if (!opcode.equals("VOTE")) {
					throw new IOException("Invalid OP Code -> "+opcode);
				}
				
				String service = readString(block, position);
				position += service.length() + 1;
				String nick = readString(block, position);
				position += nick.length() + 1;
				String ip = readString(block, position);
				position += ip.length() + 1;
				String time = readString(block, position);
				position += time.length() + 1;
				
				VoteListenerManager.addToQueue(new Vote(service, nick, ip, time));
				
				System.out.println("Received vote of "+nick+":"+ip+" from "+service+" at "+time);
				
				output.close();
				input.close();
				client.close();
			} catch (IOException ex) {
				LOG.log(Level.WARNING, "Exception at Vortifier->"+port+": "+ex.getMessage());
			}
		}
	}
	
	public void shutdown() {
		if (socket == null) {
			return;
		}
		try {
			socket.close();
		} catch (Exception ex) {}
		socket = null;
	}
}
