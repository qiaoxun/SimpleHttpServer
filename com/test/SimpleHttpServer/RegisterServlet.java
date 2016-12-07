package com.test.SimpleHttpServer;

import java.io.IOException;

public class RegisterServlet extends Servlet {

	@Override
	public void doGet(Request req, Response rep) throws Exception {
		
	}

	@Override
	public void doPost(Request req, Response rep) throws Exception {
		try {
			rep.print("<html><head></head><body>your user name is "+req.getParameterValue("name")+"</body></html>");
			rep.responseToClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
