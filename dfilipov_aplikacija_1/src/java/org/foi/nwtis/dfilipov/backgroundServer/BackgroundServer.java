package org.foi.nwtis.dfilipov.backgroundServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.foi.nwtis.dfilipov.config.ExtendedConfigManager;
import org.foi.nwtis.dfilipov.web.listeners.AppListener;

public class BackgroundServer extends Thread
{
	private final AppListener appListener;
	
	private final int port;
	private final int nThreads;
	
	private boolean running = true;
	private boolean acceptingUserCommands = true;
	
	public BackgroundServer(AppListener appListener)
	{
		super("BackgroundServer");
		this.appListener = appListener;
		
		ExtendedConfigManager config = 
			(ExtendedConfigManager) AppListener.getServletContext().getAttribute("config");
		
		this.port = Integer.parseInt(config.getBackgroundServerPort());
		this.nThreads = Integer.parseInt(config.getBackgroundServerThreadPoolSize());
	}

	public boolean isRunning()
	{
		return running;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public boolean isAcceptingUserCommands()
	{
		return acceptingUserCommands;
	}

	public void setAcceptingUserCommands(boolean acceptingUserCommands)
	{
		this.acceptingUserCommands = acceptingUserCommands;
	}
	
	@Override
	public void run()
	{
		ExecutorService requestExecutor = Executors.newFixedThreadPool(nThreads, new RequestThreadFactory());
		try (ServerSocket server = new ServerSocket(port))
		{
			server.setSoTimeout(3000);
			while (running)
			{
				try 
				{
					Socket client = server.accept();
					try
					{
						BackgroundRequestProcessor brp = new BackgroundRequestProcessor(this, client);
						requestExecutor.execute(brp);
					}
					catch (RejectedExecutionException ex)
					{
						respondWithErrorMessage(client);
					}
				}
				catch (SocketTimeoutException ex) {}
			}
			
			requestExecutor.shutdown();
			requestExecutor.awaitTermination(10, TimeUnit.SECONDS);
		}
		catch (IOException ex) 
		{
			System.out.println("Unable to process data through server socket. " + ex.getMessage());
		}
		catch (InterruptedException ex) {
			System.out.println("Background server was interrupted from terminating its threadpool.");
		}
	}
	
	private void respondWithErrorMessage(Socket client)
	{
		try (OutputStream os = client.getOutputStream())
		{
			String errorMessage = "Unable to process your request: All available request processors are busy.";
			os.write(errorMessage.getBytes());
		}
		catch (IOException ex) 
		{
			System.out.println("Unable to respond with error message");
		}
	}

	public AppListener getAppListener()
	{
		return appListener;
	}
}
