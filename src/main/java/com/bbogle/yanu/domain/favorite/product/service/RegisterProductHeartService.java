package com.bbogle.yanu.domain.favorite.product.service;

import com.bbogle.yanu.domain.favorite.product.dto.RegisterProductHeartRequestDto;
import com.bbogle.yanu.domain.favorite.product.repository.FavoriteProductRepository;
import com.bbogle.yanu.domain.user.domain.UserEntity;
import com.bbogle.yanu.domain.user.repository.UserRepository;
import com.bbogle.yanu.global.exception.HeartDuplicateException;
import com.bbogle.yanu.global.exception.error.ErrorCode;
import com.bbogle.yanu.global.jwt.TokenProvider;
import com.bbogle.yanu.global.jwt.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegisterProductHeartService {
    private final FavoriteProductRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TokenValidator tokenValidator;
    private final TokenProvider tokenProvider;

    @Transactional
    public void execute(RegisterProductHeartRequestDto request, HttpServletRequest httpRequest){
        String token = tokenValidator.validateToken(httpRequest);

        Long userId = tokenProvider.getUserId(token);
        Long productId = request.getProductId().getId();
        boolean exists = favoriteRepository.existsByUserIdAndProductId(userId, productId);

        if(exists){
            throw new HeartDuplicateException("heart duplicated", ErrorCode.HEART_DUPLICATION);
        }

        UserEntity user = userRepository.findUserById(userId);
        favoriteRepository.save(request.toEntity(user));
    }
}
