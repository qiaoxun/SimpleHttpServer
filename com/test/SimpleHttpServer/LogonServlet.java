package com.test.SimpleHttpServer;

import java.io.IOException;

public class LogonServlet extends Servlet {

	@Override
	public void doGet(Request req,Response rep) throws Exception {
		try {
			rep.print("<html><head></head><body>welcome back : "+req.getParameterValue("name")+"</body></html>");
			rep.responseToClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(Request req,Response rep) throws Exception {
		
	}

}
