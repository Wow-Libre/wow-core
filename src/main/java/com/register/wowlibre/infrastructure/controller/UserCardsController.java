package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.dto.user_card.CardItemDto;
import com.register.wowlibre.domain.dto.user_card.SendCardRequestDto;
import com.register.wowlibre.domain.dto.user_card.UserCardsDto;
import com.register.wowlibre.domain.port.in.user_card.BuyPackPort;
import com.register.wowlibre.domain.port.in.user_card.SendCardPort;
import com.register.wowlibre.domain.port.in.user_card.UserCardsPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/cards")
public class UserCardsController {

    private final UserCardsPort userCardsPort;
    private final BuyPackPort buyPackPort;
    private final SendCardPort sendCardPort;

    public UserCardsController(UserCardsPort userCardsPort, BuyPackPort buyPackPort, SendCardPort sendCardPort) {
        this.userCardsPort = userCardsPort;
        this.buyPackPort = buyPackPort;
        this.sendCardPort = sendCardPort;
    }

    /**
     * Devuelve las cartas descubiertas del usuario con código, URL de imagen y nombre.
     * El cliente usa imageUrl para mostrar la imagen (permite agregar/cambiar desde el backend).
     */
    @GetMapping
    public ResponseEntity<GenericResponse<UserCardsDto>> getDiscoveredCards(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId) {

        List<CardItemDto> cards = userCardsPort.getDiscoveredCards(userId, transactionId);
        UserCardsDto data = new UserCardsDto(cards);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }

    /**
     * Devuelve todo el catálogo de cartas (código, imagen, nombre). Define cuántas cartas existen y su orden.
     * No requiere autenticación.
     */
    @GetMapping("/catalog")
    public ResponseEntity<GenericResponse<UserCardsDto>> getCatalog() {
        List<CardItemDto> catalog = userCardsPort.getCatalog();
        UserCardsDto data = new UserCardsDto(catalog);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(data, null).ok().build());
    }

    /**
     * Compra un sobre: descuenta 200 puntos del wallet y devuelve 3 cartas al azar según probabilidad del catálogo.
     * 400 si saldo insuficiente o catálogo vacío.
     */
    @PostMapping("/buy-pack")
    public ResponseEntity<GenericResponse<UserCardsDto>> buyPack(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId) {

        List<CardItemDto> cards = buyPackPort.buyPack(userId, transactionId);
        UserCardsDto data = new UserCardsDto(cards);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(data, transactionId).ok().build());
    }

    /**
     * Envía una copia de una carta al usuario con el correo indicado (el backend lo busca por email).
     * Body: { "card_code": "CARD_001", "to_user_email": "destino@example.com" }
     */
    @PostMapping("/send")
    public ResponseEntity<GenericResponse<Void>> sendCard(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long fromUserId,
            @RequestBody SendCardRequestDto body) {

        if (body == null || body.getCardCode() == null || body.getToUserEmail() == null || body.getToUserEmail().isBlank()) {
            throw new BadRequestException("card_code y to_user_email (correo del destinatario) son obligatorios", transactionId);
        }
        sendCardPort.sendCard(fromUserId, body.getToUserEmail(), body.getCardCode(), transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseBuilder<>((Void) null, transactionId).ok().build());
    }
}
