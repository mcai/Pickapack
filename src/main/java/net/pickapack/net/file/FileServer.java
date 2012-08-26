package net.pickapack.net.file;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class FileServer {
    public static void serve(String resourceBase, int port) throws Exception {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
//        resource_handler.setWelcomeFiles(new String[]{"index.html"});

        resource_handler.setResourceBase(resourceBase);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        if(args.length == 2) {
            serve(args[0], Integer.parseInt(args[1]));
        }
        else {
            System.out.println("Usage: FileServer <folder-to-be-shared> <port>");
        }
    }
}