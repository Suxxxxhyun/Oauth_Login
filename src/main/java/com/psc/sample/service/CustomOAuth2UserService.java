package com.psc.sample.service;

import com.psc.sample.domain.UserInfo;
import com.psc.sample.domain.UserRepository;
import com.psc.sample.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    UserRepository userRepository;

    //로그인을 하고 난뒤, 세션에 정보를 넣기 위해 아래와 같이 코드한다.
    @Autowired
    HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //registrationId값이 구글이면 google, 네이버면 naver로 나옴.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String email;
        Map<String, Object> response = oAuth2User.getAttributes();

        if(registrationId.equals("naver")){
            Map<String, Object> hash = (Map<String, Object>) response.get("response");

            email = (String)hash.get("email");
        } else if(registrationId.equals("google")){
            email = (String)response.get("email");
        } else {
            throw new OAuth2AuthenticationException("허용되지 않은 인증입니다.");
        }

        UserInfo user;
        Optional<UserInfo> optionalUser = userRepository.findByEmail(email);

        //이미 구글이나 네이버로 가입한 사람이라면
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        //가입하지 않은 사람이라면 DB에 저장
        else {
            user = new UserInfo();
            user.setEmail(email);
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
        }
        //세션에 user라는 이름으로 user등록
        httpSession.setAttribute("user",user);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()))
                , oAuth2User.getAttributes()
                , userNameAttributeName);
    }
}
