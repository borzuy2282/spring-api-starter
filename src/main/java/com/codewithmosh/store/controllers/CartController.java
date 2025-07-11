package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.*;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriComponentsBuilder
    ){
        var cartDto = cartService.createCart();
        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Adds the product to the cart.")
    public ResponseEntity<CartItemDto> addItem(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartDto request
            ){
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(
        @PathVariable UUID cartId
    ){
        return cartService.getCart(cartId);

    }


    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateCartItem(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId,
            @Parameter(description = "The ID of the product in the cart.")
            @PathVariable Long productId,
            @Valid @RequestBody UpdateItemDto request
            ){
        return cartService.updateCartItem(cartId, productId, request.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId,
            @Parameter(description = "The ID of the product in the cart.")
            @PathVariable Long productId
    ){
        cartService.deleteCartItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "The ID of the cart")
            @PathVariable UUID cartId
    ){
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCartNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto("Cart was not found.")
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Product was not found in the cart.")
        );
    }



}
