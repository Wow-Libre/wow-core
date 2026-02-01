package com.register.wowlibre.application.services.products;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.packages.*;
import com.register.wowlibre.domain.port.in.product_category.*;
import com.register.wowlibre.domain.port.in.product_details.*;
import com.register.wowlibre.domain.port.in.products.*;
import com.register.wowlibre.domain.port.out.products.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
public class ProductService implements ProductPort {
    private final ObtainProduct products;
    private final SaveProduct saveProducts;
    private final RandomString randomString;

    private final ProductDetailsPort productDetailsPort;
    private final ProductCategoryPort productCategoryPort;
    private final PackagesPort packagesPort;

    public ProductService(ObtainProduct products, SaveProduct saveProducts, ProductDetailsPort productDetailsPort,
                          @Qualifier("productReference") RandomString randomString,
                          ProductCategoryPort productCategoryPort, PackagesPort packagesPort) {
        this.products = products;
        this.saveProducts = saveProducts;
        this.productDetailsPort = productDetailsPort;
        this.randomString = randomString;
        this.productCategoryPort = productCategoryPort;
        this.packagesPort = packagesPort;
    }

    @Override
    public Map<String, List<ProductCategoryModel>> products(String language, String transactionId) {

        List<ProductEntity> productsDb = products.findByStatusIsTrueAndLanguage(language, transactionId);

        return productsDb.stream()
                .collect(Collectors.groupingBy(
                        product -> product.getProductCategoryId().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                productList -> {
                                    ProductEntity firstProduct = productList.getFirst();
                                    return List.of(new ProductCategoryModel(
                                            firstProduct.getProductCategoryId().getId(),
                                            firstProduct.getProductCategoryId().getName(),
                                            firstProduct.getProductCategoryId().getDescription(),
                                            firstProduct.getProductCategoryId().getDisclaimer(),
                                            productList.stream()
                                                    .map(this::mapToModel).toList()
                                    ));
                                }
                        )
                ));
    }

    private ProductsDto mapToModel(ProductEntity product) {
        Integer discount = product.getDiscount();
        Double price = product.getPrice();

        return ProductsDto.builder()
                .id(product.getId())
                .name(product.getName())
                .disclaimer(product.getDisclaimer())
                .discountPrice(calculateFinalPrice(price, discount))
                .price(price)
                .discount(discount)
                .usePoints(product.isUseCreditPoints())
                .description(product.getDescription())
                .imgUrl(product.getImageUrl())
                .partner(product.getRealmName())
                .referenceNumber(product.getReferenceNumber())
                .category(product.getProductCategoryId().getName())
                .build();
    }

    public Double calculateFinalPrice(Double price, Integer discount) {
        Double discountAmount = price * (Optional.of(discount).orElse(0) / 100.0);
        return price - discountAmount;
    }


    @Override
    public ProductDto product(String referenceCode, String transactionId) {
        ProductEntity productModel = getProduct(referenceCode, transactionId);

        List<ProductDetailModel> productDetails = productDetailsPort.findByProductId(productModel, transactionId);

        return ProductDto.builder().id(productModel.getId())
                .name(productModel.getName())
                .disclaimer(productModel.getDisclaimer())
                .price(productModel.getPrice())
                .discount(productModel.getDiscount())
                .usePoints(productModel.isUseCreditPoints())
                .description(productModel.getDescription())
                .imgUrl(productModel.getImageUrl())
                .partner(productModel.getRealmName())
                .referenceNumber(productModel.getReferenceNumber())
                .details(productDetails)
                .serverId(productModel.getRealmId())
                .category(productModel.getProductCategoryId().getName()).build();

    }

    @Override
    public ProductEntity getProduct(String referenceCode, String transactionId) {
        Optional<ProductEntity> product = products.findByReferenceNumber(referenceCode, transactionId);

        if (product.isEmpty()) {
            throw new InternalException("Product Not Found", transactionId);
        }

        return product.get();
    }

    @Override
    public List<ProductDiscountsDto> productDiscounts(String language, String transactionId) {


        return products.findByStatusIsTrueAndDiscount(language, transactionId).stream()
                .map(productModel -> ProductDiscountsDto.builder().id(productModel.getId())
                        .name(productModel.getName())
                        .disclaimer(productModel.getDisclaimer())
                        .price(productModel.getPrice())
                        .discountPrice(calculateFinalPrice(productModel.getPrice(),
                                productModel.getDiscount()))
                        .discount(productModel.getDiscount())
                        .usePoints(productModel.isUseCreditPoints())
                        .description(productModel.getDescription())
                        .imgUrl(productModel.getImageUrl())
                        .partner(productModel.getRealmName())
                        .referenceNumber(productModel.getReferenceNumber())
                        .serverId(productModel.getRealmId())
                        .category(productModel.getProductCategoryId().getName()).build()).toList();
    }

    @Override
    public void createProduct(CreateProductDto product, String transactionId) {
        final String name = product.getName();
        final String language = product.getLanguage();
        final Long productCategoryId = product.getProductCategoryId();

        products.findByNameAndLanguage(name, language, transactionId)
                .ifPresent(existingProduct -> {
                    throw new InternalException("Product with name '" + name
                            + "' already exists in language '" + language + "'", transactionId);
                });

        ProductCategoryEntity productCategory = productCategoryPort.findById(productCategoryId, transactionId);

        if (productCategory == null) {
            throw new InternalException("Product Category with id '" + productCategoryId + "' does not exist",
                    transactionId);
        }

        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(name);
        productEntity.setDisclaimer(product.getDisclaimer());
        productEntity.setDescription(product.getDescription());
        productEntity.setPrice(product.getPrice());
        productEntity.setDiscount(product.getDiscount());
        productEntity.setTax(product.getTax());
        productEntity.setReturnTax(product.getReturnTax());
        productEntity.setUseCreditPoints(product.getCreditPointsEnabled());
        productEntity.setImageUrl(product.getImageUrl());
        productEntity.setCreditPointsValue(product.getCreditPointsValue());
        productEntity.setLanguage(language);
        productEntity.setReferenceNumber(randomString.nextString());
        productEntity.setRealmId(product.getRealmId());
        productEntity.setProductCategoryId(productCategory);
        productEntity.setRealmName(product.getRealmName());
        productEntity.setLanguage(product.getLanguage());
        productEntity.setStatus(true);
        saveProducts.save(productEntity, transactionId);

        if (product.getPackages() != null && !product.getPackages().isEmpty()) {
            product.getPackages().forEach(packageCode -> {
                PackagesEntity packageEntity = new PackagesEntity();
                packageEntity.setCodeCore(packageCode);
                packageEntity.setProductId(productEntity);
                packagesPort.save(packageEntity, transactionId);
            });
        }
    }

    @Override
    public ProductsDetailsDto allProducts(String transactionId) {
        List<ProductEntity> productsDb = products.findAllByStatusIsTrue(transactionId);

        return ProductsDetailsDto.builder().products(productsDb.stream().map(product -> ProductModel.builder()
                .id(product.getId())
                .name(product.getName())
                .disclaimer(product.getDisclaimer())
                .category(product.getProductCategoryId().getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .pointsAmount(product.getCreditPointsValue())
                .discount(product.getDiscount())
                .discountPrice(calculateFinalPrice(product.getPrice(), product.getDiscount()))
                .usePoints(product.isUseCreditPoints())
                .categoryId(product.getProductCategoryId().getId())
                .categoryName(product.getProductCategoryId().getName())
                .description(product.getDescription())
                .imgUrl(product.getImageUrl())
                .language(product.getLanguage())
                .partner(product.getRealmName())
                .partnerId(product.getRealmId())
                .tax(product.getTax())
                .returnTax(product.getReturnTax())
                .referenceNumber(product.getReferenceNumber())
                .build()).toList()).totalProducts(productsDb.size()).build();
    }

    @Override
    public void deleteProduct(Long productId, String transactionId) {
        Optional<ProductEntity> product = products.findById(productId, transactionId);

        if (product.isEmpty()) {
            throw new InternalException("Product Not Found", transactionId);
        }

        ProductEntity productEntity = product.get();
        productEntity.setName(productEntity.getName() + "_deleted_" + System.currentTimeMillis());
        productEntity.setStatus(false);
        saveProducts.save(productEntity, transactionId);
    }

}
