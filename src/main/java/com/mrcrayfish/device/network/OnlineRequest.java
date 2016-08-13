package com.mrcrayfish.device.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mrcrayfish.device.util.StreamUtils;

public class OnlineRequest implements Runnable
{
	private static OnlineRequest instance = null;
	
	private boolean running = true;
	
	private Queue<RequestWrapper> requests;
	
	private Thread thread;
	
	private OnlineRequest() 
	{
		this.requests = new ConcurrentLinkedQueue<RequestWrapper>();
		start();
	}
	
	public static OnlineRequest getInstance() 
	{
		if(instance == null) 
		{
			instance = new OnlineRequest();
		}
		return instance;
	}
	
	public void start() 
	{
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run()
	{
		while(running) 
		{
			while(requests.size() > 0)
			{
				RequestWrapper wrapper = requests.poll();
				try 
				{
					HttpURLConnection connection = (HttpURLConnection) new URL(wrapper.url).openConnection();
		            connection.connect();
		            InputStream input = connection.getInputStream();
		            String response = StreamUtils.convertToString(input);
		            wrapper.handler.handle(response);
				} 
				catch(Exception e) {}
			}
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void make(String url, ResponseHandler handler) 
	{
		this.requests.offer(new RequestWrapper(url, handler));
		if(this.requests.size() > 0) 
		{
			thread.notify();
		}
	}
	
	private static class RequestWrapper 
	{
		public final String url;
		public final ResponseHandler handler;
		
		public RequestWrapper(String url, ResponseHandler handler)
		{
			this.url = url;
			this.handler = handler;
		}
	}
	
	public interface ResponseHandler 
	{
		public void handle(String response);
	}
}
