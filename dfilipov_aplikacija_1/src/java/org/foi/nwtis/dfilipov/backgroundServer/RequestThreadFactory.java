package org.foi.nwtis.dfilipov.backgroundServer;

import java.util.concurrent.ThreadFactory;

public class RequestThreadFactory implements ThreadFactory
{
	private static int threadNumber = 1;
	
	@Override
	public Thread newThread(Runnable r)
	{
		String threadName = "RequestProcessor-" + threadNumber++;
		return new Thread(r, threadName);
	}
}
