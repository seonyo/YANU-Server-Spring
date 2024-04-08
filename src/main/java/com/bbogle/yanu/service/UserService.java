package com.bbogle.yanu.service;

import com.bbogle.yanu.dto.user.LoginRequestDto;
import com.bbogle.yanu.dto.user.PasswordUpdateRequestDto;
import com.bbogle.yanu.dto.user.RegisterRequestDto;
import com.bbogle.yanu.entity.UserEntity;
import com.bbogle.yanu.exception.EmailDuplicateException;
import com.bbogle.yanu.exception.PasswordNotFoundException;
import com.bbogle.yanu.exception.PhoneNumberDuplicateException;
import com.bbogle.yanu.exception.UserNotFoundException;
import com.bbogle.yanu.exception.error.ErrorCode;
import com.bbogle.yanu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequestDto request){
        String email = request.getEmail();
        String phonenumber = request.getPhonenumber();
        String password = passwordEncoder.encode(request.getPassword());

        duplicateEmail(email);
        duplicatePhonenumber(phonenumber);

        userRepository.save(request.toEntity(password));
    }
    public void duplicateEmail(String email){
        if(userRepository.existsByEmail(email)){
            throw new EmailDuplicateException("email duplicated", ErrorCode.EMAIL_DUPLICATION);
        }
    }

    public void duplicatePhonenumber(String phonenumber){
        if(userRepository.existsByPhonenumber(phonenumber)){
            throw new PhoneNumberDuplicateException("phone duplicated", ErrorCode.PHONENUMBER_DUPLICATION);
        }
    }

    public Long loginUser(LoginRequestDto request){
        String email = request.getEmail();
        String password = request.getPassword();

        checkEmail(email);

        UserEntity userEntity = userRepository.findByEmail(email);
        checkPassword(password, userEntity.getPassword());

        return userEntity.getId();
    }
    private void checkEmail(String email){
        if(!userRepository.existsByEmail(email)){
            throw new EmailDuplicateException("email not found", ErrorCode.EMAIL_NOTFOUND);
        }
    }
    private void checkPassword(String password, String hashedPassword){
        if(!passwordEncoder.matches(password, hashedPassword)){
            throw new PasswordNotFoundException("password not found", ErrorCode.PASSWORD_NOTFOUND);
        }
    }

    public UserEntity findByUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found", ErrorCode.USER_NOTFOUND));
    }

    public void updatePassword(PasswordUpdateRequestDto request){
        String email = request.getEmail();
        String password = request.getPassword();
        checkEmail(email);

        UserEntity userEntity = userRepository.findByEmail(email);
        String encodedPassword = passwordEncoder.encode(password);

        userEntity.setPassword(encodedPassword);
        userRepository.save(userEntity);
    }

}