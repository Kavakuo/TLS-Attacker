/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol;

import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;

/**
 * @param <T>
 * The ProtocolMessage that should be prepared
 */
public abstract class ProtocolMessagePreparator<T extends ProtocolMessage> extends Preparator<T> {

    private final ProtocolMessage message;

    public ProtocolMessagePreparator(Chooser chooser, T message) {
        super(chooser, message);
        this.message = message;
    }

    @Override
    public final void prepare() {
        prepareProtocolMessageContents();
    }

    protected abstract void prepareProtocolMessageContents();
}
