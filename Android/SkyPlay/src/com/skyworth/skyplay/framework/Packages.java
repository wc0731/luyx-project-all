package com.skyworth.skyplay.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Packages {
	
	public static byte[] toBytes(Object obj) {  
		if(obj != null) {
	        ByteArrayOutputStream bis = null;  
	        ObjectOutputStream os = null;  
	        try {  
	            bis = new ByteArrayOutputStream(1024);  
	            os = new ObjectOutputStream(bis);  
	            os.writeObject(obj);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
				try {
		            if (bis != null)
						bis.close();
		            if (os != null)  
		                os.close();  
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	        }  
	        return bis.toByteArray();
		}
		return null;
    }  
  
    public static Object toPackage(byte[] src) {  
    	if(src != null) {
	        ObjectInputStream ois = null;  
	        ByteArrayInputStream bos = null;  
	        try {  
	            bos = new ByteArrayInputStream(src);  
	            ois = new ObjectInputStream(bos);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
				try {
		            if (bos != null)  
		                bos.close();  
		            if (ois != null)  
		                ois.close();  
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	        }  
	        Object obj = null;
			try {
				obj = ois.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return obj;  
    	}
    	return null;
    }  

}
