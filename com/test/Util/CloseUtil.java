package com.test.Util;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtil {
	public static void closeAll(Closeable... ios){
		for(Closeable io : ios){
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
