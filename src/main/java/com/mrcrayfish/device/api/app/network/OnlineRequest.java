package com.mrcrayfish.device.api.app.network;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mrcrayfish.device.util.StreamUtils;

public class OnlineRequest
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
	
	/**
	 * Gets a singleton instance of OnlineRequest
	 * 
	 * @return the singleton OnlineRequest object
	 */
	public static OnlineRequest getInstance() 
	{
		if(instance == null) 
		{
			instance = new OnlineRequest();
		}
		return instance;
	}
	
	private void start() 
	{
		thread = new Thread(new RequestRunnable(), "OnlineRequest");
		thread.start();
	}
	
	/**
	 * Adds a request to the queue
	 * 
	 * @param url the URL you want to make a request to
	 * @param handler the response handler for the request
	 */
	public void make(String url, ResponseHandler handler) 
	{
		requests.offer(new RequestWrapper(url, handler));
	}
	
	private class RequestRunnable implements Runnable 
	{
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
						System.out.println("Making request to: " + wrapper.url);
						HttpURLConnection connection = (HttpURLConnection) new URL(wrapper.url).openConnection();
			            connection.connect();
			            InputStream input = connection.getInputStream();
			            String response = StreamUtils.convertToString(input);
			            wrapper.handler.handle(true, response);
					} 
					catch(Exception e) 
					{
						wrapper.handler.handle(false, null);
					}
				}
			}
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
		/**
		 * Handles the response from an OnlineRequest
		 * 
		 * @param success if the request was successful or not
		 * @param response the response from the request. null if success is false
		 */
		public void handle(boolean success, String response);
	}
}
