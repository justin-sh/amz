package com.lu.justin.tool.file.web;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;

public class TomcatCustomerConnector implements TomcatConnectorCustomizer {
    @Override
    public void customize(Connector connector) {
        ProtocolHandler handler = connector.getProtocolHandler();
        if (handler instanceof AbstractHttp11Protocol) {
            ((AbstractHttp11Protocol<?>) handler).setMaxSwallowSize(11*1024*1024);
        }

    }
}
