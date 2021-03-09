/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.exceptions;

/**
 * Unknown cipher suite exception
 */
public class UnknownCipherSuiteException extends RuntimeException {

    public UnknownCipherSuiteException() {
        super();
    }

    public UnknownCipherSuiteException(String message) {
        super(message);
    }

    public UnknownCipherSuiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
