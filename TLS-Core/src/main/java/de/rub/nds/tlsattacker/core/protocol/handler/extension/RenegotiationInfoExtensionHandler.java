/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import static de.rub.nds.tlsattacker.core.protocol.handler.extension.ExtensionHandler.LOGGER;
import de.rub.nds.tlsattacker.core.protocol.message.extension.RenegotiationInfoExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.RenegotiationInfoExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.RenegotiationInfoExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.RenegotiationInfoExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;

/**
 *
 * @author Matthias Terlinde <matthias.terlinde@rub.de>
 */
public class RenegotiationInfoExtensionHandler extends ExtensionHandler<RenegotiationInfoExtensionMessage> {

    public RenegotiationInfoExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public RenegotiationInfoExtensionParser getParser(byte[] message, int pointer) {
        return new RenegotiationInfoExtensionParser(pointer, message);
    }

    @Override
    public RenegotiationInfoExtensionPreparator getPreparator(RenegotiationInfoExtensionMessage message) {
        return new RenegotiationInfoExtensionPreparator(context.getChooser(), message, getSerializer(message));
    }

    @Override
    public RenegotiationInfoExtensionSerializer getSerializer(RenegotiationInfoExtensionMessage message) {
        return new RenegotiationInfoExtensionSerializer(message);
    }

    @Override
    public void adjustTLSContext(RenegotiationInfoExtensionMessage message) {
        if (message.getExtensionLength().getValue() > 65535) {
            LOGGER.warn("The RenegotiationInfo length shouldn't exceed 2 bytes as defined in RFC 5246. "
                    + "Length was " + message.getExtensionLength().getValue());
        }
        if (context.getTalkingConnectionEndType() != context.getConfig().getConnectionEndType()) {
            context.setRenegotiationInfo(message.getRenegotiationInfo().getValue());
        }
        if (context.getTalkingConnectionEndType() == ConnectionEndType.SERVER) {
            if (message.getRenegotiationInfo().getValue().length == 1
                    && message.getRenegotiationInfo().getValue()[0] == 0) {
                context.setIsSecureRenegotiation(true);
            }
        }
        LOGGER.debug("The context RenegotiationInfo was set to "
                + ArrayConverter.bytesToHexString(message.getRenegotiationInfo()));
    }

}