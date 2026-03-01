package com.register.wowlibre.domain.port.out.user_card;

/**
 * Puerto para decrementar (o eliminar) una carta del usuario (al enviarla a otro).
 */
public interface DecrementUserCard {

    /**
     * Decrementa en 1 la cantidad de la carta; si queda en 0 elimina la fila.
     * @return true si el usuario tenía al menos una copia, false si no tenía esa carta.
     */
    boolean decrement(Long userId, String cardCode);
}
