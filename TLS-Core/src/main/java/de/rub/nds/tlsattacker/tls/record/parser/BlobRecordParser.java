/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tls.record.parser;

import de.rub.nds.tlsattacker.tls.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.tls.record.BlobRecord;

/**
 *
 * @author Robert Merget <robert.merget@rub.de>
 */
public class BlobRecordParser extends AbstractRecordParser<BlobRecord> {

    public BlobRecordParser(int startposition, byte[] array, ProtocolVersion version) {
        super(startposition, array, version);
    }

    @Override
    public BlobRecord parse() {
        BlobRecord record = new BlobRecord();
        record.setProtocolMessageBytes(parseByteArrayField(getBytesLeft()));
        return record;
    }
}