/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.https.header;

import de.rub.nds.tlsattacker.core.https.header.preparator.DateHeaderPreparator;
import de.rub.nds.tlsattacker.core.protocol.Preparator;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;

public class DateHeader extends HttpsHeader {

    public DateHeader() {
    }

    @Override
    public DateHeaderPreparator getPreparator(Chooser chooser) {
        return new DateHeaderPreparator(chooser, this);
    }

}
