package org.jor.jetty;

import org.jor.LoginConstantsForTests;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * used through ant for system tests.
 * 
 */
public class JettyStart implements LoginConstantsForTests
{
    private static final Logger LOG = LoggerFactory.getLogger(JettyStart.class);
    
    private static Server server;

    public static void main(String[] args) throws Exception
    {
        server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(LoginConstantsForTests.TEST_PORT);
        server.setConnectors(new Connector[] { connector });
        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath(DEFAULT_CONTEXT_PATH);
        context.setWar(DEFAULT_WAR_DIR);
        server.addHandler(context);
        Thread monitor = new MonitorThread();
        monitor.start();
        server.start();
        server.join();
    }

    private static class MonitorThread extends Thread implements LoginConstantsForTests
    {
        private ServerSocket socket;

        public MonitorThread()
        {
            super("Stop Monitor");
            setDaemon(true);
            try
            {
                socket = new ServerSocket(DEFAULT_STOP_PORT, 1, InetAddress.getByName(LOCALHOST_IP));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run()
        {
            LOG.info("Running jetty 'stop' thread");
            Socket accept;
            try
            {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
                LOG.info("Stopping jetty embedded server");
                server.stop();
                accept.close();
                socket.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
