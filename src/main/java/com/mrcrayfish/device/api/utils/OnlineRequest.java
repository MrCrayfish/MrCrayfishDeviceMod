package com.mrcrayfish.device.api.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mrcrayfish.device.util.StreamUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * OnlineRequest is a simple built in request system for handling URL connections.
 * It runs in the background so it doesn't freeze the user interface of your application.
 * All requests are returned with a string, use how you please!
 * 
 * @author MrCrayfish
 */
public class OnlineRequest
{
	private static OnlineRequest instance = null;

	private final Queue<RequestWrapper> requests;

	private Thread thread;
	private boolean running = true;

	private OnlineRequest() 
	{
		this.requests = new ConcurrentLinkedQueue<>();
		start();
	}
	
	/**
	 * Gets a singleton instance of OnlineRequest. Use this instance to
	 * start making requests.
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
		thread = new Thread(new RequestRunnable(), "Online Request Thread");
		thread.start();
	}
	
	/**
	 * Adds a request to the queue. Use the handler to process the
	 * response you get from the URL connection.
	 * 
	 * @param url the URL you want to make a request to
	 * @param handler the response handler for the request
	 */
	public void make(String url, ResponseHandler handler)
	{
		synchronized(requests)
		{
			requests.offer(new RequestWrapper(url, handler));
			requests.notify();
		}
	}
	
	private class RequestRunnable implements Runnable 
	{
		@Override
		public void run()
		{
			while(running) 
			{
				try
				{
					synchronized(requests)
					{
						requests.wait();
					}
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}

				while(!requests.isEmpty())
				{
					RequestWrapper wrapper = requests.poll();
					try(CloseableHttpClient client = HttpClients.createDefault())
					{
						HttpGet get = new HttpGet(wrapper.url);
						try(CloseableHttpResponse response = client.execute(get))
						{
							String raw = StreamUtils.convertToString(response.getEntity().getContent());
							wrapper.handler.handle(true, raw);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						wrapper.handler.handle(false, "");
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
		void handle(boolean success, String response);
	}
}
