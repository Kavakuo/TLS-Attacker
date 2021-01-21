/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.certificate.transparency.logs;

public class CtLogListLoader {

    private static CtLogList logList;

    public static CtLogList loadLogList() {
        if (logList == null) {
            logList = new ChromeCtLogListParser().parseLogList("ct/log_list.json");
        }
        return logList;
    }
}
