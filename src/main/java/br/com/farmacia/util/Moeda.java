package br.com.farmacia.util;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Formata valores monetários sempre no padrão brasileiro ("R$ 43,18"),
 * independentemente do idioma configurado na máquina.
 */
public final class Moeda {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Moeda() {
    }

    public static String formatar(BigDecimal valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }
}
