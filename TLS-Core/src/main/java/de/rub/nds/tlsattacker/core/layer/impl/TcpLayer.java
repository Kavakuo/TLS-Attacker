/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.layer.impl;

import de.rub.nds.tlsattacker.core.layer.DataContainer;
import de.rub.nds.tlsattacker.core.layer.LayerConfiguration;
import de.rub.nds.tlsattacker.core.layer.hints.LayerProcessingHint;
import de.rub.nds.tlsattacker.core.layer.LayerProcessingResult;
import de.rub.nds.tlsattacker.core.layer.ProtocolLayer;
import de.rub.nds.tlsattacker.core.layer.stream.HintedInputStream;
import de.rub.nds.tlsattacker.core.layer.stream.HintedInputStreamAdapterStream;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.transport.tcp.TcpTransportHandler;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpLayer extends ProtocolLayer<LayerProcessingHint, DataContainer> {// TODO change types

    private static Logger LOGGER = LogManager.getLogger();

    private final TlsContext context;

    /**
     * TODO: This should be replaced - I dont think its necesarry to have the transport module at all after this
     */
    private TcpTransportHandler handler;

    public TcpLayer(TlsContext context) {
        this.context = context;
        if (context.getTransportHandler() == null) {
            throw new RuntimeException("TransportHandler is not set in context!");
        }
        if (!(context.getTransportHandler() instanceof TcpTransportHandler)) {
            throw new RuntimeException("Trying to set TCP layer with non TCP TransportHandler");
        }
        handler = (TcpTransportHandler) context.getTransportHandler();

    }

    @Override
    public LayerProcessingResult sendData(byte[] data) throws IOException {
        return sendData(null, data);// no hint needed
    }

    @Override
    public LayerProcessingResult sendData() throws IOException {
        LayerConfiguration<DataContainer> configuration = getLayerConfiguration();
        if (configuration.getContainerList() != null) {
            for (DataContainer container : configuration.getContainerList()) {
                // TODO Send container data
            }
        }
        return getLayerResult();
    }

    @Override
    public LayerProcessingResult sendData(LayerProcessingHint hint) throws IOException {
        LayerConfiguration<DataContainer> configuration = getLayerConfiguration();
        for (DataContainer container : configuration.getContainerList()) {
            // TODO Send container data
        }
        throw new UnsupportedOperationException("Implement configureable TCP packet container");
    }

    @Override
    public LayerProcessingResult sendData(LayerProcessingHint hint, byte[] data) throws IOException {
        if (handler.getOutputStream() == null) {
            throw new RuntimeException("TCP Layer not initialized");
        }
        handler.getOutputStream().write(data);
        getDataForHigherLayerStream().write(data);
        return new LayerProcessingResult(null, null);// Not implemented
    }

    @Override
    public byte[] retrieveMoreData(LayerProcessingHint hint) throws IOException {
        if (handler.getInputStream() == null) {
            throw new RuntimeException("TCP Layer not initialized");
        }
        byte[] data = new byte[handler.getInputStream().available()];
        handler.getInputStream().read(data);
        getDataForHigherLayerStream().write(data);
        return data;
    }

    @Override
    public HintedInputStream getDataStream() {
        return new HintedInputStreamAdapterStream(null, handler.getInputStream());
    }

    @Override
    public void preInititialize() throws IOException {
        handler.preInitialize();
    }

    @Override
    public void inititialize() throws IOException {
        handler.initialize();
    }

    @Override
    public LayerProcessingResult receiveData() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }
}
