package com.mrcrayfish.device.api.io;

public class DataException extends RuntimeException 
{
	private final String message;
	
	public DataException(String message) 
	{
		this.message = message;
	}
	
	@Override
	public String getMessage() 
	{
		return message;
	}
}
