package org.foi.nwtis.dfilipov.web.singleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;

@Singleton
@LocalBean
public class MessageBufferBean
{
	private final List<Serializable> messageBuffer = new ArrayList<>();
	
	public List<Serializable> getMessageBuffer()
	{
		return messageBuffer;
	}
	
	public void addMessage(Serializable message)
	{
		messageBuffer.add(message);
	}
}
