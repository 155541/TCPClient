package com.revolhope.deepdev.tcpclient.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import com.revolhope.deepdev.tcplibrary.model.Device;

public class Toolkit 
{
	public static Device thisDevice;
	public static ArrayList<Device> connectedDevices = new ArrayList<>();
	
	/**
	 * 
	 * @return
	 */
	public static String getMacAddress()
	{
		NetworkInterface network;
		try 
		{ 
			network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) 
			{
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			return sb.toString();
		} 
		catch (SocketException | UnknownHostException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean existsConfigFile()
	{
		File f = new File(Params.pathConfigFile);
		return f.exists() && !f.isDirectory();
	}
	
	/**
	 * 
	 * @param devName
	 * @param devHomeDir
	 * @return
	 */
	public static boolean writeConfigFile(String devName, String devHomeDir)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(Params.pathConfigFile);
			StringBuilder sb = new StringBuilder();
			sb.append(devName).append(Params.separator).append(devHomeDir);
			
			fos.write(sb.toString().getBytes(Charset.forName("UTF-8")));
			fos.flush();
			fos.close();
			
			return true;
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String[] readConfigFile()
	{
		try 
		{
			FileInputStream fis = new FileInputStream(Params.pathConfigFile);
			byte[] data = new byte[10240];
			fis.read(data, 0, data.length);
			String txt = new String(data,Charset.forName("UTF-8"));
			fis.close();
			return new String[] {txt.split(Params.separator)[0], txt.split(Params.separator)[1]};
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static long timestamp()
	{
		return Calendar.getInstance().getTimeInMillis();
	}
}
