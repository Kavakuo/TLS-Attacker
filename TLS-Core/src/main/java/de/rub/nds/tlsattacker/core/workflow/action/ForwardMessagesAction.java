/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.modifiablevariable.HoldsModifiableVariable;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolMessageType;
import de.rub.nds.tlsattacker.core.exceptions.ConfigurationException;
import de.rub.nds.tlsattacker.core.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.core.https.HttpsRequestMessage;
import de.rub.nds.tlsattacker.core.https.HttpsResponseMessage;
import de.rub.nds.tlsattacker.core.layer.LayerConfiguration;
import de.rub.nds.tlsattacker.core.layer.LayerProcessingResult;
import de.rub.nds.tlsattacker.core.layer.LayerStack;
import de.rub.nds.tlsattacker.core.layer.SpecificContainerLayerConfiguration;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessageHandler;
import de.rub.nds.tlsattacker.core.protocol.message.*;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class ForwardMessagesAction extends TlsAction implements ReceivingAction, SendingAction {

    private static final Logger LOGGER = LogManager.getLogger();

    @XmlElement(name = "from")
    protected String receiveFromAlias = null;
    @XmlElement(name = "to")
    protected String forwardToAlias = null;

    @XmlTransient
    protected Boolean executedAsPlanned = null;

    /**
     * If you want true here, use the more verbose ForwardMessagesWithPrepareAction.
     */
    @XmlTransient
    protected Boolean withPrepare = false;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = ProtocolMessage.class, name = "ProtocolMessage"),
        @XmlElement(type = CertificateMessage.class, name = "Certificate"),
        @XmlElement(type = CertificateVerifyMessage.class, name = "CertificateVerify"),
        @XmlElement(type = CertificateRequestMessage.class, name = "CertificateRequest"),
        @XmlElement(type = CertificateStatusMessage.class, name = "CertificateStatus"),
        @XmlElement(type = ClientHelloMessage.class, name = "ClientHello"),
        @XmlElement(type = HelloVerifyRequestMessage.class, name = "HelloVerifyRequest"),
        @XmlElement(type = DHClientKeyExchangeMessage.class, name = "DHClientKeyExchange"),
        @XmlElement(type = DHEServerKeyExchangeMessage.class, name = "DHEServerKeyExchange"),
        @XmlElement(type = ECDHClientKeyExchangeMessage.class, name = "ECDHClientKeyExchange"),
        @XmlElement(type = ECDHEServerKeyExchangeMessage.class, name = "ECDHEServerKeyExchange"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PSKClientKeyExchange"),
        @XmlElement(type = PWDServerKeyExchangeMessage.class, name = "PWDServerKeyExchange"),
        @XmlElement(type = PWDClientKeyExchangeMessage.class, name = "PWDClientKeyExchange"),
        @XmlElement(type = FinishedMessage.class, name = "Finished"),
        @XmlElement(type = RSAClientKeyExchangeMessage.class, name = "RSAClientKeyExchange"),
        @XmlElement(type = GOSTClientKeyExchangeMessage.class, name = "GOSTClientKeyExchange"),
        @XmlElement(type = ServerHelloDoneMessage.class, name = "ServerHelloDone"),
        @XmlElement(type = ServerHelloMessage.class, name = "ServerHello"),
        @XmlElement(type = AlertMessage.class, name = "Alert"),
        @XmlElement(type = NewSessionTicketMessage.class, name = "NewSessionTicket"),
        @XmlElement(type = KeyUpdateMessage.class, name = "KeyUpdate"),
        @XmlElement(type = ApplicationMessage.class, name = "Application"),
        @XmlElement(type = ChangeCipherSpecMessage.class, name = "ChangeCipherSpec"),
        @XmlElement(type = SSL2ClientHelloMessage.class, name = "SSL2ClientHello"),
        @XmlElement(type = SSL2ServerHelloMessage.class, name = "SSL2ServerHello"),
        @XmlElement(type = SSL2ClientMasterKeyMessage.class, name = "SSL2ClientMasterKey"),
        @XmlElement(type = SSL2ServerVerifyMessage.class, name = "SSL2ServerVerify"),
        @XmlElement(type = UnknownMessage.class, name = "UnknownMessage"),
        @XmlElement(type = UnknownHandshakeMessage.class, name = "UnknownHandshakeMessage"),
        @XmlElement(type = HelloRequestMessage.class, name = "HelloRequest"),
        @XmlElement(type = HeartbeatMessage.class, name = "Heartbeat"),
        @XmlElement(type = SupplementalDataMessage.class, name = "SupplementalDataMessage"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensionMessage"),
        @XmlElement(type = HttpsRequestMessage.class, name = "HttpsRequest"),
        @XmlElement(type = HttpsResponseMessage.class, name = "HttpsResponse"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PskClientKeyExchange"),
        @XmlElement(type = PskDhClientKeyExchangeMessage.class, name = "PskDhClientKeyExchange"),
        @XmlElement(type = PskDheServerKeyExchangeMessage.class, name = "PskDheServerKeyExchange"),
        @XmlElement(type = PskEcDhClientKeyExchangeMessage.class, name = "PskEcDhClientKeyExchange"),
        @XmlElement(type = PskEcDheServerKeyExchangeMessage.class, name = "PskEcDheServerKeyExchange"),
        @XmlElement(type = PskRsaClientKeyExchangeMessage.class, name = "PskRsaClientKeyExchange"),
        @XmlElement(type = PskServerKeyExchangeMessage.class, name = "PskServerKeyExchange"),
        @XmlElement(type = SrpServerKeyExchangeMessage.class, name = "SrpServerKeyExchange"),
        @XmlElement(type = SrpClientKeyExchangeMessage.class, name = "SrpClientKeyExchange"),
        @XmlElement(type = EndOfEarlyDataMessage.class, name = "EndOfEarlyData"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensions") })
    protected List<ProtocolMessage> receivedMessages;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = Record.class, name = "Record") })
    protected List<Record> receivedRecords;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = DtlsHandshakeMessageFragment.class, name = "DtlsFragment") })
    protected List<DtlsHandshakeMessageFragment> receivedFragments;

    @XmlElementWrapper
    @HoldsModifiableVariable
    @XmlElements(value = { @XmlElement(type = ProtocolMessage.class, name = "ProtocolMessage"),
        @XmlElement(type = CertificateMessage.class, name = "Certificate"),
        @XmlElement(type = CertificateVerifyMessage.class, name = "CertificateVerify"),
        @XmlElement(type = CertificateRequestMessage.class, name = "CertificateRequest"),
        @XmlElement(type = ClientHelloMessage.class, name = "ClientHello"),
        @XmlElement(type = HelloVerifyRequestMessage.class, name = "HelloVerifyRequest"),
        @XmlElement(type = DHClientKeyExchangeMessage.class, name = "DHClientKeyExchange"),
        @XmlElement(type = DHEServerKeyExchangeMessage.class, name = "DHEServerKeyExchange"),
        @XmlElement(type = ECDHClientKeyExchangeMessage.class, name = "ECDHClientKeyExchange"),
        @XmlElement(type = ECDHEServerKeyExchangeMessage.class, name = "ECDHEServerKeyExchange"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PSKClientKeyExchange"),
        @XmlElement(type = FinishedMessage.class, name = "Finished"),
        @XmlElement(type = RSAClientKeyExchangeMessage.class, name = "RSAClientKeyExchange"),
        @XmlElement(type = GOSTClientKeyExchangeMessage.class, name = "GOSTClientKeyExchange"),
        @XmlElement(type = ServerHelloDoneMessage.class, name = "ServerHelloDone"),
        @XmlElement(type = ServerHelloMessage.class, name = "ServerHello"),
        @XmlElement(type = AlertMessage.class, name = "Alert"),
        @XmlElement(type = NewSessionTicketMessage.class, name = "NewSessionTicket"),
        @XmlElement(type = KeyUpdateMessage.class, name = "KeyUpdate"),
        @XmlElement(type = ApplicationMessage.class, name = "Application"),
        @XmlElement(type = ChangeCipherSpecMessage.class, name = "ChangeCipherSpec"),
        @XmlElement(type = SSL2ClientHelloMessage.class, name = "SSL2ClientHello"),
        @XmlElement(type = SSL2ServerHelloMessage.class, name = "SSL2ServerHello"),
        @XmlElement(type = SSL2ClientMasterKeyMessage.class, name = "SSL2ClientMasterKey"),
        @XmlElement(type = SSL2ServerVerifyMessage.class, name = "SSL2ServerVerify"),
        @XmlElement(type = UnknownMessage.class, name = "UnknownMessage"),
        @XmlElement(type = UnknownHandshakeMessage.class, name = "UnknownHandshakeMessage"),
        @XmlElement(type = HelloRequestMessage.class, name = "HelloRequest"),
        @XmlElement(type = HeartbeatMessage.class, name = "Heartbeat"),
        @XmlElement(type = SupplementalDataMessage.class, name = "SupplementalDataMessage"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensionMessage"),
        @XmlElement(type = HttpsRequestMessage.class, name = "HttpsRequest"),
        @XmlElement(type = HttpsResponseMessage.class, name = "HttpsResponse"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PskClientKeyExchange"),
        @XmlElement(type = PskDhClientKeyExchangeMessage.class, name = "PskDhClientKeyExchange"),
        @XmlElement(type = PskDheServerKeyExchangeMessage.class, name = "PskDheServerKeyExchange"),
        @XmlElement(type = PskEcDhClientKeyExchangeMessage.class, name = "PskEcDhClientKeyExchange"),
        @XmlElement(type = PskEcDheServerKeyExchangeMessage.class, name = "PskEcDheServerKeyExchange"),
        @XmlElement(type = PskRsaClientKeyExchangeMessage.class, name = "PskRsaClientKeyExchange"),
        @XmlElement(type = PskServerKeyExchangeMessage.class, name = "PskServerKeyExchange"),
        @XmlElement(type = SrpServerKeyExchangeMessage.class, name = "SrpServerKeyExchange"),
        @XmlElement(type = SrpClientKeyExchangeMessage.class, name = "SrpClientKeyExchange"),
        @XmlElement(type = EndOfEarlyDataMessage.class, name = "EndOfEarlyData"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensions") })
    protected List<ProtocolMessage> messages;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = Record.class, name = "Record") })
    protected List<Record> records;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = DtlsHandshakeMessageFragment.class, name = "DtlsFragment") })
    protected List<DtlsHandshakeMessageFragment> fragments;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = ProtocolMessage.class, name = "ProtocolMessage"),
        @XmlElement(type = CertificateMessage.class, name = "Certificate"),
        @XmlElement(type = CertificateVerifyMessage.class, name = "CertificateVerify"),
        @XmlElement(type = CertificateRequestMessage.class, name = "CertificateRequest"),
        @XmlElement(type = ClientHelloMessage.class, name = "ClientHello"),
        @XmlElement(type = HelloVerifyRequestMessage.class, name = "HelloVerifyRequest"),
        @XmlElement(type = DHClientKeyExchangeMessage.class, name = "DHClientKeyExchange"),
        @XmlElement(type = DHEServerKeyExchangeMessage.class, name = "DHEServerKeyExchange"),
        @XmlElement(type = ECDHClientKeyExchangeMessage.class, name = "ECDHClientKeyExchange"),
        @XmlElement(type = ECDHEServerKeyExchangeMessage.class, name = "ECDHEServerKeyExchange"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PSKClientKeyExchange"),
        @XmlElement(type = FinishedMessage.class, name = "Finished"),
        @XmlElement(type = RSAClientKeyExchangeMessage.class, name = "RSAClientKeyExchange"),
        @XmlElement(type = GOSTClientKeyExchangeMessage.class, name = "GOSTClientKeyExchange"),
        @XmlElement(type = ServerHelloDoneMessage.class, name = "ServerHelloDone"),
        @XmlElement(type = ServerHelloMessage.class, name = "ServerHello"),
        @XmlElement(type = AlertMessage.class, name = "Alert"),
        @XmlElement(type = NewSessionTicketMessage.class, name = "NewSessionTicket"),
        @XmlElement(type = KeyUpdateMessage.class, name = "KeyUpdate"),
        @XmlElement(type = ApplicationMessage.class, name = "Application"),
        @XmlElement(type = ChangeCipherSpecMessage.class, name = "ChangeCipherSpec"),
        @XmlElement(type = SSL2ClientHelloMessage.class, name = "SSL2ClientHello"),
        @XmlElement(type = SSL2ServerHelloMessage.class, name = "SSL2ServerHello"),
        @XmlElement(type = SSL2ClientMasterKeyMessage.class, name = "SSL2ClientMasterKey"),
        @XmlElement(type = SSL2ServerVerifyMessage.class, name = "SSL2ServerVerify"),
        @XmlElement(type = UnknownMessage.class, name = "UnknownMessage"),
        @XmlElement(type = UnknownHandshakeMessage.class, name = "UnknownHandshakeMessage"),
        @XmlElement(type = HelloRequestMessage.class, name = "HelloRequest"),
        @XmlElement(type = HeartbeatMessage.class, name = "Heartbeat"),
        @XmlElement(type = SupplementalDataMessage.class, name = "SupplementalDataMessage"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensionMessage"),
        @XmlElement(type = HttpsRequestMessage.class, name = "HttpsRequest"),
        @XmlElement(type = HttpsResponseMessage.class, name = "HttpsResponse"),
        @XmlElement(type = PskClientKeyExchangeMessage.class, name = "PskClientKeyExchange"),
        @XmlElement(type = PskDhClientKeyExchangeMessage.class, name = "PskDhClientKeyExchange"),
        @XmlElement(type = PskDheServerKeyExchangeMessage.class, name = "PskDheServerKeyExchange"),
        @XmlElement(type = PskEcDhClientKeyExchangeMessage.class, name = "PskEcDhClientKeyExchange"),
        @XmlElement(type = PskEcDheServerKeyExchangeMessage.class, name = "PskEcDheServerKeyExchange"),
        @XmlElement(type = PskRsaClientKeyExchangeMessage.class, name = "PskRsaClientKeyExchange"),
        @XmlElement(type = PskServerKeyExchangeMessage.class, name = "PskServerKeyExchange"),
        @XmlElement(type = SrpServerKeyExchangeMessage.class, name = "SrpServerKeyExchange"),
        @XmlElement(type = SrpClientKeyExchangeMessage.class, name = "SrpClientKeyExchange"),
        @XmlElement(type = EndOfEarlyDataMessage.class, name = "EndOfEarlyData"),
        @XmlElement(type = EncryptedExtensionsMessage.class, name = "EncryptedExtensions") })
    protected List<ProtocolMessage> sendMessages;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = Record.class, name = "Record") })
    protected List<Record> sendRecords;

    @HoldsModifiableVariable
    @XmlElementWrapper
    @XmlElements(value = { @XmlElement(type = DtlsHandshakeMessageFragment.class, name = "DtlsFragment") })
    protected List<DtlsHandshakeMessageFragment> sendFragments;

    public ForwardMessagesAction() {
    }

    public ForwardMessagesAction(String receiveFromAlias, String forwardToAlias, List<ProtocolMessage> messages) {
        this.messages = messages;
        this.receiveFromAlias = receiveFromAlias;
        this.forwardToAlias = forwardToAlias;
    }

    public ForwardMessagesAction(String receiveFromAlias, String forwardToAlias, ProtocolMessage... messages) {
        this(receiveFromAlias, forwardToAlias, new ArrayList<>(Arrays.asList(messages)));
    }

    public void setReceiveFromAlias(String receiveFromAlias) {
        this.receiveFromAlias = receiveFromAlias;
    }

    public void setForwardToAlias(String forwardToAlias) {
        this.forwardToAlias = forwardToAlias;
    }

    @Override
    public void execute(State state) throws WorkflowExecutionException {
        if (isExecuted()) {
            throw new WorkflowExecutionException("Action already executed!");
        }

        assertAliasesSetProperly();

        TlsContext receiveFromCtx = state.getTlsContext(receiveFromAlias);
        TlsContext forwardToCtx = state.getTlsContext(forwardToAlias);

        receiveMessages(receiveFromCtx);
        applyMessages(forwardToCtx);
        forwardMessages(forwardToCtx);
        setExecuted(true);
    }

    void receiveMessages(TlsContext receiveFromContext) {
        LOGGER.debug("Receiving Messages...");
        LayerStack layerStack = receiveFromContext.getLayerStack();
        List<LayerConfiguration> layerConfigurationList = new LinkedList<>();
        layerConfigurationList.add(new SpecificContainerLayerConfiguration(messages));
        layerConfigurationList.add(new SpecificContainerLayerConfiguration((List) null));
        layerConfigurationList.add(new SpecificContainerLayerConfiguration((List) null));
        List<LayerProcessingResult> processingResult;
        try {
            processingResult = layerStack.receiveData(layerConfigurationList);
            receivedMessages = new ArrayList<>(processingResult.get(0).getUsedContainers()); // TODO Automatically get
            // correct
            // index in result
            receivedRecords = new ArrayList<>(processingResult.get(1).getUsedContainers()); // TODO Automatically get
            // correct
            // index in result
        } catch (IOException ex) {
            LOGGER.warn("Received an IOException");
        }
        String expected = getReadableString(receivedMessages);
        LOGGER.debug("Receive Expected (" + receiveFromAlias + "): " + expected);
        String received = getReadableString(receivedMessages);
        LOGGER.info("Received Messages (" + receiveFromAlias + "): " + received);

        executedAsPlanned = checkMessageListsEquals(messages, receivedMessages);
    }

    /**
     * Apply the contents of the messages to the given TLS context.
     *
     * @param protocolMessages
     * @param tlsContext
     */
    private void applyMessages(TlsContext ctx) {
        for (ProtocolMessage msg : receivedMessages) {
            LOGGER.debug("Applying " + msg.toCompactString() + " to forward context " + ctx);
            ProtocolMessageHandler h = msg.getHandler(ctx);
            h.adjustContext(msg);
        }
    }

    private void forwardMessages(TlsContext forwardToCtx) {
        LOGGER.info("Forwarding messages (" + forwardToAlias + "): " + getReadableString(messages));
        try {
            LayerStack layerStack = forwardToCtx.getLayerStack();
            List<LayerConfiguration> layerConfigurationList = new LinkedList<>();
            layerConfigurationList.add(new SpecificContainerLayerConfiguration(messages));
            layerConfigurationList.add(new SpecificContainerLayerConfiguration(records));
            layerConfigurationList.add(new SpecificContainerLayerConfiguration((List) null));
            List<LayerProcessingResult> processingResult = layerStack.sendData(layerConfigurationList);
            messages = new ArrayList<>(processingResult.get(0).getUsedContainers()); // TODO Automatically get
            // correct index in result
            records = new ArrayList<>(processingResult.get(1).getUsedContainers()); // TODO Automatically get
            // correct index in result

            if (executedAsPlanned) {
                executedAsPlanned = checkMessageListsEquals(sendMessages, messages);
            }
            setExecuted(true);
        } catch (IOException e) {
            LOGGER.debug(e);
            executedAsPlanned = false;
            setExecuted(false);
        }
    }

    public String getReceiveFromAlias() {
        return receiveFromAlias;
    }

    public String getForwardToAlias() {
        return forwardToAlias;
    }

    // TODO: yes, the correct way would be implement equals() for all
    // ProtocolMessages...
    private boolean checkMessageListsEquals(List<ProtocolMessage> expectedMessages,
        List<ProtocolMessage> actualMessages) {
        boolean actualEmpty = true;
        boolean expectedEmpty = true;
        if (actualMessages != null && !actualMessages.isEmpty()) {
            actualEmpty = false;
        }
        if (expectedMessages != null && !expectedMessages.isEmpty()) {
            expectedEmpty = false;
        }
        if (actualEmpty == expectedEmpty) {
            return true;
        }
        if (actualEmpty != expectedEmpty) {
            return false;
        }
        if (actualMessages.size() != expectedMessages.size()) {
            return false;
        } else {
            for (int i = 0; i < actualMessages.size(); i++) {
                if (!actualMessages.get(i).getClass().equals(expectedMessages.get(i).getClass())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean executedAsPlanned() {
        return executedAsPlanned;
    }

    @Override
    public void reset() {
        receivedMessages = null;
        receivedRecords = null;
        receivedFragments = null;
        sendMessages = null;
        sendRecords = null;
        sendFragments = null;
        executedAsPlanned = false;
        setExecuted(null);
    }

    @Override
    public List<ProtocolMessage> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public List<Record> getReceivedRecords() {
        return receivedRecords;
    }

    @Override
    public List<DtlsHandshakeMessageFragment> getReceivedFragments() {
        return receivedFragments;
    }

    @Override
    public List<ProtocolMessage> getSendMessages() {
        return sendMessages;
    }

    @Override
    public List<Record> getSendRecords() {
        return sendRecords;
    }

    @Override
    public List<DtlsHandshakeMessageFragment> getSendFragments() {
        return sendFragments;
    }

    public List<ProtocolMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ProtocolMessage> messages) {
        this.messages = messages;
    }

    public void setMessages(ProtocolMessage... messages) {
        this.messages = new ArrayList(Arrays.asList(messages));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.receiveFromAlias);
        hash = 89 * hash + Objects.hashCode(this.forwardToAlias);
        hash = 89 * hash + Objects.hashCode(this.executedAsPlanned);
        hash = 89 * hash + Objects.hashCode(this.receivedMessages);
        hash = 89 * hash + Objects.hashCode(this.receivedRecords);
        hash = 89 * hash + Objects.hashCode(this.receivedFragments);
        hash = 89 * hash + Objects.hashCode(this.sendMessages);
        hash = 89 * hash + Objects.hashCode(this.sendRecords);
        hash = 89 * hash + Objects.hashCode(this.messages);
        hash = 89 * hash + Objects.hashCode(this.records);
        hash = 89 * hash + Objects.hashCode(this.fragments);
        return hash;
    }

    /**
     * TODO: the equals methods for message/record actions and similar classes would require that messages and records
     * implement equals for a proper implementation. The present approach is not satisfying.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ForwardMessagesAction other = (ForwardMessagesAction) obj;
        if (!Objects.equals(this.receiveFromAlias, other.receiveFromAlias)) {
            return false;
        }
        if (!Objects.equals(this.forwardToAlias, other.forwardToAlias)) {
            return false;
        }
        if (!Objects.equals(this.executedAsPlanned, other.executedAsPlanned)) {
            return false;
        }
        if (!checkMessageListsEquals(this.receivedMessages, other.receivedMessages)) {
            return false;
        }
        if (!Objects.equals(this.receivedRecords, other.receivedRecords)) {
            return false;
        }
        if (!Objects.equals(this.receivedFragments, other.receivedFragments)) {
            return false;
        }
        if (!checkMessageListsEquals(this.sendMessages, other.sendMessages)) {
            return false;
        }
        if (!Objects.equals(this.sendRecords, other.sendRecords)) {
            return false;
        }
        if (!Objects.equals(this.sendFragments, other.sendFragments)) {
            return false;
        }
        if (!checkMessageListsEquals(this.messages, other.messages)) {
            return false;
        }
        return Objects.equals(this.records, other.records);
    }

    @Override
    public Set<String> getAllAliases() {
        Set<String> aliases = new LinkedHashSet<>();
        aliases.add(forwardToAlias);
        aliases.add(receiveFromAlias);
        return aliases;
    }

    @Override
    public void assertAliasesSetProperly() throws ConfigurationException {
        if ((receiveFromAlias == null) || (receiveFromAlias.isEmpty())) {
            throw new WorkflowExecutionException("Can't execute " + this.getClass().getSimpleName()
                + " with empty receive alias (if using XML: add <from/>)");
        }
        if ((forwardToAlias == null) || (forwardToAlias.isEmpty())) {
            throw new WorkflowExecutionException("Can't execute " + this.getClass().getSimpleName()
                + " with empty forward alis (if using XML: add <to/>)");
        }
    }

    public String getReadableString(List<ProtocolMessage> messages) {
        return getReadableString(messages, false);
    }

    public String getReadableString(List<ProtocolMessage> messages, Boolean verbose) {
        StringBuilder builder = new StringBuilder();
        if (messages == null) {
            return builder.toString();
        }
        for (ProtocolMessage message : messages) {
            if (verbose) {
                builder.append(message.toString());
            } else {
                builder.append(message.toCompactString());
            }
            if (!message.isRequired()) {
                builder.append("*");
            }
            builder.append(", ");
        }
        return builder.toString();
    }

    @Override
    public void normalize() {
        super.normalize();
        initEmptyLists();
    }

    @Override
    public void normalize(TlsAction defaultAction) {
        super.normalize(defaultAction);
        initEmptyLists();
    }

    @Override
    public void filter() {
        super.filter();
        stripEmptyLists();
    }

    @Override
    public void filter(TlsAction defaultAction) {
        super.filter(defaultAction);
        stripEmptyLists();
    }

    private void stripEmptyLists() {
        if (messages == null || messages.isEmpty()) {
            messages = null;
        }
        if (records == null || records.isEmpty()) {
            records = null;
        }
        if (fragments == null || fragments.isEmpty()) {
            fragments = null;
        }
        if (receivedMessages == null || receivedMessages.isEmpty()) {
            receivedMessages = null;
        }
        if (receivedRecords == null || receivedRecords.isEmpty()) {
            receivedRecords = null;
        }
        if (receivedFragments == null || receivedFragments.isEmpty()) {
            receivedFragments = null;
        }
        if (sendMessages == null || sendMessages.isEmpty()) {
            sendMessages = null;
        }
        if (sendRecords == null || sendRecords.isEmpty()) {
            sendRecords = null;
        }
        if (sendFragments == null || sendFragments.isEmpty()) {
            sendFragments = null;
        }
    }

    private void initEmptyLists() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        if (records == null) {
            records = new ArrayList<>();
        }
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (receivedMessages == null) {
            receivedMessages = new ArrayList<>();
        }
        if (receivedRecords == null) {
            receivedRecords = new ArrayList<>();
        }
        if (receivedFragments == null) {
            receivedFragments = new ArrayList<>();
        }
        if (sendMessages == null) {
            sendMessages = new ArrayList<>();
        }
        if (sendRecords == null) {
            sendRecords = new ArrayList<>();
        }
        if (sendFragments == null) {
            sendFragments = new ArrayList<>();
        }
    }

    @Override
    public List<ProtocolMessageType> getGoingToReceiveProtocolMessageTypes() {
        if (this.messages == null) {
            return new ArrayList<>();
        }

        List<ProtocolMessageType> types = new ArrayList<>();
        for (ProtocolMessage msg : messages) {
            types.add(msg.getProtocolMessageType());
        }
        return types;
    }

    @Override
    public List<HandshakeMessageType> getGoingToReceiveHandshakeMessageTypes() {
        if (this.messages == null) {
            return new ArrayList<>();
        }

        List<HandshakeMessageType> types = new ArrayList<>();
        for (ProtocolMessage msg : messages) {
            if (!(msg instanceof HandshakeMessage)) {
                continue;
            }
            types.add(((HandshakeMessage) msg).getHandshakeMessageType());
        }
        return types;
    }

    @Override
    public List<ProtocolMessageType> getGoingToSendProtocolMessageTypes() {
        return this.getGoingToReceiveProtocolMessageTypes();
    }

    @Override
    public List<HandshakeMessageType> getGoingToSendHandshakeMessageTypes() {
        return this.getGoingToReceiveHandshakeMessageTypes();
    }
}
