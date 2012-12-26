package org.jor.jetty;

import org.jor.LoginConstantsForTests;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class JettyStop implements LoginConstantsForTests
{
    private static final Logger LOG = LoggerFactory.getLogger(JettyStop.class);
    
    public static void main(String[] args) throws Exception
    {
        try (Socket s = new Socket(InetAddress.getByName(LOCALHOST_IP), DEFAULT_STOP_PORT);)
        {
	        OutputStream out = s.getOutputStream();
	        LOG.info("Sending jetty stop request");
	        out.write(("\r\n").getBytes());
	        out.flush();
        }
    }

}
