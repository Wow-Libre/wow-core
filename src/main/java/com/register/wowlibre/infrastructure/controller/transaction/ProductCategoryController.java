package com.register.wowlibre.infrastructure.controller.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.product_category.ProductCategoryPort;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    private final ProductCategoryPort productCategoryPort;

    public ProductCategoryController(ProductCategoryPort productCategoryPort) {
        this.productCategoryPort = productCategoryPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<ProductCategoryDto>>> all(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        List<ProductCategoryDto> productCategories = productCategoryPort.findAllProductCategories(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(productCategories, transactionId).created().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid CreateProductCategoryDto request) {

        productCategoryPort.createProductCategory(request.getName(), request.getDescription(),
                request.getDisclaimer(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
