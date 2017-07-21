/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.SignedCertificateTimestampExtensionMessage;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import de.rub.nds.tlsattacker.core.state.TlsContext;

/**
 *
 * @author Matthias Terlinde <matthias.terlinde@rub.de>
 */
public class SignedCertificateTimestampExtensionPreparator extends
        ExtensionPreparator<SignedCertificateTimestampExtensionMessage> {

    private final SignedCertificateTimestampExtensionMessage message;

    /**
     * Constructor
     * 
     * @param chooser
     * @param message
     */
    public SignedCertificateTimestampExtensionPreparator(Chooser chooser,
            SignedCertificateTimestampExtensionMessage message) {
        super(chooser, message);
        this.message = message;
    }

    /**
     * Parses a SignedCertificateTimestampExtensionMessage of a TLSContext.
     */
    @Override
    public void prepareExtensionContent() {
        message.setSignedTimestamp(chooser.getConfig().getDefaultSignedCertificateTimestamp());
        LOGGER.debug("Prepared the SignedCertificateTimestapExtension with timestamp length "
                + message.getSignedTimestamp().getValue().length);
    }

}
