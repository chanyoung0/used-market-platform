package com.chanyoung.usedmarket.domain.product;

import com.chanyoung.usedmarket.domain.product.dto.ProductCreateRequestDto;
import com.chanyoung.usedmarket.domain.product.dto.ProductResponseDto;
import com.chanyoung.usedmarket.domain.product.dto.ProductUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> createProduct(@AuthenticationPrincipal Long memberId, @Valid @RequestBody ProductCreateRequestDto request) {
        Long productId = productService.createProduct(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId){
        ProductResponseDto response = productService.getProduct(productId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@AuthenticationPrincipal Long memberId, @PathVariable Long productId ,@Valid @RequestBody ProductUpdateRequestDto request) {
        ProductResponseDto response = productService.updateProduct(memberId, productId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal Long memberId, @PathVariable Long productId) {
        productService.deleteProduct(memberId, productId);
        return ResponseEntity.noContent().build();
    }


}
