package com.bbogle.yanu.domain.cart.controller;

import com.bbogle.yanu.domain.cart.dto.DeleteCartRequestDto;
import com.bbogle.yanu.domain.cart.dto.FindCartResponseDto;
import com.bbogle.yanu.domain.cart.dto.RegisterCartRequestDto;
import com.bbogle.yanu.domain.cart.domain.CartEntity;
import com.bbogle.yanu.domain.cart.service.DeleteCartService;
import com.bbogle.yanu.domain.cart.service.FindCartService;
import com.bbogle.yanu.domain.cart.service.RegisterCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final RegisterCartService registerCartService;
    private final DeleteCartService deleteCartService;
    private final FindCartService findCartService;

    @PostMapping
    public ResponseEntity<String> registerCart(@RequestBody RegisterCartRequestDto requst, HttpServletRequest httpRequest){
        registerCartService.execute(requst, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("장바구니 등록에 성공했습니다.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCart(@RequestBody DeleteCartRequestDto request, HttpServletRequest httpRequest){
        deleteCartService.execute(request, httpRequest);
        return ResponseEntity.ok().body("장바구니에서 삭제 성공했습니다.");
    }

    @GetMapping
    public List<FindCartResponseDto> findCart(HttpServletRequest httpRequest){
        List<CartEntity> carts = findCartService.execute(httpRequest);
        return carts.stream()
                .map(FindCartResponseDto::new)
                .collect(Collectors.toList());
    }
}
