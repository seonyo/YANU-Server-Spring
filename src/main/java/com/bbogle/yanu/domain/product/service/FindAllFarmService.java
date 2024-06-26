package com.bbogle.yanu.domain.product.service;

import com.bbogle.yanu.domain.farm.repository.FarmRepository;
import com.bbogle.yanu.domain.product.domain.ProductEntity;
import com.bbogle.yanu.domain.product.domain.ProductImageEntity;
import com.bbogle.yanu.domain.product.dto.ProductAllResponseDto;
import com.bbogle.yanu.domain.product.dto.ProductFarmResponseDto;
import com.bbogle.yanu.domain.product.facade.ProductFacade;
import com.bbogle.yanu.domain.product.repository.ProductImageRepository;
import com.bbogle.yanu.domain.product.repository.ProductRepository;
import com.bbogle.yanu.global.exception.FarmNotFoundException;
import com.bbogle.yanu.global.exception.TokenNotFoundException;
import com.bbogle.yanu.global.exception.error.ErrorCode;
import com.bbogle.yanu.global.jwt.TokenProvider;
import com.bbogle.yanu.global.jwt.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FindAllFarmService {
    private final ProductRepository productRepository;
    private final FarmRepository farmRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductFacade productFacade;
    private final TokenValidator tokenValidator;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public List<ProductFarmResponseDto> execute(Long farmId, HttpServletRequest httpRequest){
        String token = tokenValidator.validateToken(httpRequest);

        Long userId =  tokenProvider.getUserId(token);

        boolean exist = farmRepository.existsById(farmId);
        if(!exist)
            throw new FarmNotFoundException("farm not found", ErrorCode.FARM_NOTFOUND);

        List<ProductEntity> products = productRepository.findAllByFarmId(farmId);
        List<Long> productIds = products.stream().map(ProductEntity::getId).collect(Collectors.toList());
        List<ProductImageEntity> images = productImageRepository.findByProductIdIn(productIds);

        return products.stream()
                .map(product -> new ProductFarmResponseDto(product,
                        productFacade.checkIsHeart(product, userId),
                        images))
                .collect(Collectors.toList());
    }
}
