/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolMessageType;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.record.AbstractRecord;

import java.util.ArrayList;
import java.util.List;

public interface SendingAction {
    public abstract List<ProtocolMessage> getSendMessages();

    public abstract List<AbstractRecord> getSendRecords();

    public default List<ProtocolMessageType> getGoingToSendProtocolMessageTypes() {
        return new ArrayList<>();
    }

    public default List<HandshakeMessageType> getGoingToSendHandshakeMessageTypes() {
        return new ArrayList<>();
    }
}
