package iwb.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import iwb.cache.FrameworkSetting;
import iwb.exception.IWBException;
import sun.net.www.protocol.ftp.FtpURLConnection;

public class FtpUtil {

	public static String send(String targetURL) {
		FtpURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (FtpURLConnection) url.openConnection();

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request


			// Get Response
			boolean success = connection.getContentLength() != 0;
			
			if(success) {
				InputStream is = connection.getInputStream();

				
				BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				String line;
				StringBuilder response = new StringBuilder();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				return response.toString();
			}
			return null;

		} catch (Exception e) {
			if(FrameworkSetting.debug)e.printStackTrace();
			 throw new IWBException(
			          "framework",
			          "HTTPUtil.send",
			          0,
			          null,
			          targetURL ,
			          e);
			
//			throw ne;
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static byte[] send4bin(String targetURL) {
		FtpURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (FtpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			

			// Get Response
			InputStream is = connection.getInputStream();
			
			
			int maxLen = 32*1024*1024; //32mb
			byte[] buffer = new byte[maxLen];
		    int bytesRead, offset = 0;
		    while ((bytesRead = is.read(buffer, offset, 1024)) != -1 && offset < maxLen-1024) {
		        offset += bytesRead;
		    }
		    byte[] result = new byte[offset];
			for(int qi=0;qi<offset;qi++)result[qi] = buffer[qi]; 
		    return result;

		} catch (Exception e) {
			if(FrameworkSetting.debug)e.printStackTrace();
			 throw new IWBException(
			          "framework",
			          "HTTPUtil.send",
			          0,
			          null,
			          targetURL,
			          e);
			
//			throw ne;
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
}

