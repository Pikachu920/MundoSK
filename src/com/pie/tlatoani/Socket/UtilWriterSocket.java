package com.pie.tlatoani.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import ch.njol.skript.lang.function.Functions;

public class UtilWriterSocket implements Runnable{
	private String[] msgs;
	private String host;
	private int port;
	private String redirect;
	private String report;
	private int timeout;
	
	public UtilWriterSocket(String[] msgsarg, String hostarg, Integer portarg, String redirectarg, String reportarg, Integer timeoutarg) {
		msgs = msgsarg;
		host = hostarg;
		port = portarg;
		redirect = redirectarg;
		report = reportarg;
		if(timeoutarg == null) timeout = 0;
		else timeout = timeoutarg;
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), timeout);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			for (int b = 0; b < msgs.length; b++) {
				writer.write(msgs[b]);
				writer.newLine();
			}
			writer.flush();
			socket.shutdownOutput();
			if (redirect != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				List<String> list = new LinkedList<String>();
				String line;
				while ((line = reader.readLine()) != null) {
					list.add(line);
				}
				Object[][] args = new Object[2][1];
				Object[] argslist = new Object[list.size()];
				for (int a = 0; a < list.size(); a++) {
					argslist[a] = list.get(a);
				}
				args[0] = argslist;
				Object[] argsinfo = new Object[3];
				argsinfo[0] = report;
				argsinfo[1] = host;
				argsinfo[2] = port;
				args[1] = argsinfo;
				Functions.getFunction(redirect).execute(args);
			}
		} catch (Exception e) {}
		finally {
			try {
				if (socket != null) socket.close();	
				
			} catch (Exception e) {}
		}
		
	}

}
