package com.revolhope.deepdev.tcpclient.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtil 
{
	
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
}
