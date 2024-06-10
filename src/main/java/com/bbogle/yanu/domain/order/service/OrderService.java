package com.bbogle.yanu.domain.order.service;

import com.bbogle.yanu.domain.cart.repository.CartRepository;
import com.bbogle.yanu.domain.farm.domain.FarmEntity;
import com.bbogle.yanu.domain.farm.repository.FarmRepository;
import com.bbogle.yanu.domain.order.domain.OrderEntity;
import com.bbogle.yanu.domain.order.dto.OrderRequestDto;
import com.bbogle.yanu.domain.order.repository.OrderRepository;
import com.bbogle.yanu.domain.product.domain.ProductEntity;
import com.bbogle.yanu.domain.product.repository.ProductRepository;
import com.bbogle.yanu.domain.sale.domain.SaleEntity;
import com.bbogle.yanu.domain.sale.repository.SaleRepository;
import com.bbogle.yanu.domain.user.domain.UserEntity;
import com.bbogle.yanu.domain.user.repository.UserRepository;
import com.bbogle.yanu.global.exception.ProductNotFoundException;
import com.bbogle.yanu.global.exception.UserNotFoundException;
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
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final SaleRepository saleRepository;
    private final FarmRepository farmRepository;
    private final ProductRepository productRepository;
    private final TokenValidator tokenValidator;
    private final TokenProvider tokenProvider;

    @Transactional
    public void execute(OrderRequestDto request, HttpServletRequest httpRequest){
        String token = tokenValidator.validateToken(httpRequest);

        Long userId = tokenProvider.getUserId(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found", ErrorCode.USER_NOTFOUND));

        List<OrderEntity> orderList = request.toEntity(user);

        List<ProductEntity> products = orderList.stream()
                .map(order -> productRepository.findById(order.getProduct().getId())
                        .orElseThrow(() -> new ProductNotFoundException("product not found", ErrorCode.PRODUCT_NOTFOUND)))
                .collect(Collectors.toList());

        // 주문 저장
        orderRepository.saveAll(orderList);

        // 판매 도메인에 저장
        List<SaleEntity> saleList = request.toSaleEntity(products);
        saleRepository.saveAll(saleList);

        // 장바구니에서 해당 내역이 있다면 제거
        for (OrderEntity order : orderList) {
            cartRepository.findByUserAndProduct(user, order.getProduct()).ifPresent(cart -> {
                cartRepository.deleteByUserAndProduct(user, order.getProduct());
            });
        }

        //ugly_percent 업데이트
        for (ProductEntity product : products) {
            FarmEntity farm = product.getFarm();
            farm.updateUglyPercent(0.2F);
            farmRepository.save(farm);
        }

    }
}