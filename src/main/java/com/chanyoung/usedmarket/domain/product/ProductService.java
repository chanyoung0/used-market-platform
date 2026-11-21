package com.chanyoung.usedmarket.domain.product;

import com.chanyoung.usedmarket.domain.member.Member;
import com.chanyoung.usedmarket.domain.member.MemberRepository;
import com.chanyoung.usedmarket.domain.member.exception.MemberNotFoundException;
import com.chanyoung.usedmarket.domain.product.dto.ProductCreateRequestDto;
import com.chanyoung.usedmarket.domain.product.dto.ProductResponseDto;
import com.chanyoung.usedmarket.domain.product.dto.ProductUpdateRequestDto;
import com.chanyoung.usedmarket.domain.product.exception.ProductAccessDeniedException;
import com.chanyoung.usedmarket.domain.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;





    @Transactional
    public Long createProduct(Long memberId, ProductCreateRequestDto request) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Product product = Product.createProduct(member, request.getTitle(), request.getDescription(), request.getCategory(), request.getPrice(), request.getImageUrl());
        productRepository.save(product);
        return product.getId();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = findProduct(productId);
        return ProductResponseDto.from(product);

    }

    @Transactional
    public ProductResponseDto updateProduct(Long memberId, Long productId, ProductUpdateRequestDto request) {
        Product product = findProduct(productId);

        if (!product.isSeller(memberId)){
            throw new ProductAccessDeniedException();
        }

        product.updateProduct(request.getTitle(), request.getDescription(), request.getCategory(), request.getPrice(), request.getImageUrl());
        return ProductResponseDto.from(product);
    }

    @Transactional
    public void deleteProduct(Long memberId, Long productId) {
        Product product = findProduct(productId);

        if (!product.isSeller(memberId)){
            throw new ProductAccessDeniedException();
        }

        product.validateDeletable();
        productRepository.delete(product);
    }

    private @NonNull Product findProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }


}
