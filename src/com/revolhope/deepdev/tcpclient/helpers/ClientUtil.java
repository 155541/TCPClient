package com.revolhope.deepdev.tcpclient.helpers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

import com.revolhope.deepdev.tcplibrary.constants.Params;
import com.revolhope.deepdev.tcplibrary.model.Device;
import com.revolhope.deepdev.tcplibrary.model.Token;

public class ClientUtil 
{
	private static Device thisDevice;
	private static Token thisToken;
	private static String thisMacAddr = null;
	private static String thisHomePath = null;
	
	public static Device getDevice()
	{
		return ClientUtil.thisDevice;
	}
	
	public static void setDevice(Device dev)
	{
		ClientUtil.thisDevice = dev;
	}
	
	public static Token getToken()
	{
		return ClientUtil.thisToken;
	}
	
	public static void setToken(Token token)
	{
		ClientUtil.thisToken = token;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getMacAddress()
	{
		if (thisMacAddr == null)
		{
			try 
			{ 
				NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
				byte[] mac = network.getHardwareAddress();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) 
				{
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				thisMacAddr = sb.toString();
			} 
			catch (SocketException | UnknownHostException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		return thisMacAddr;
	}
	
	public static InetAddress getServerAddr() throws UnknownHostException
	{
		return InetAddress.getByName(Params.SERVER_ADDRESS);
	}
	
	public static void setHomePath(String path)
	{
		ClientUtil.thisHomePath = path.trim();
	}
	
	public static String getHomePath()
	{
		return ClientUtil.thisHomePath;
	}
	
	public static long timestamp()
	{
		return Calendar.getInstance().getTimeInMillis();
	}
}
