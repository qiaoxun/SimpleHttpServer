package com.test.SimpleHttpServer;

import java.io.IOException;

public class ErrorServlet extends Servlet {

	@Override
	public void doGet(Request req,Response rep) throws Exception {
		try {
			rep.print("<html><head></head><body>url not exists,please try another</body></html>");
			rep.responseToClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(Request req,Response rep) throws Exception {
		
	}

}
