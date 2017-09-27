package org.foi.nwtis.dfilipov.web.data;

public class AddressCount
{
	private int count;
	private Address address;

	public AddressCount() {}

	public AddressCount(int count, Address address)
	{
		this.count = count;
		this.address = address;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int rank)
	{
		this.count = rank;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}
}
