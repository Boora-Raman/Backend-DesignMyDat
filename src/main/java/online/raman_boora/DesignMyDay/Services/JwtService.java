package online.raman_boora.DesignMyDay.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import online.raman_boora.DesignMyDay.Models.Users;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey;

@Component
public class JwtService {

    private final AuthenticationManager authenticationManager;

    private String secretKey = null;

    public JwtService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public String getSecretKey()
    {
        return  secretKey = "HqA15wiaV87p5yf9KhbMeiZj/ixoQpZMgvbxKedilDk=";
    }
    public String generateToken(Users user) {
        Map<String,Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(user.getName())
                .issuer("DesignMyDay")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration( new Date(System.currentTimeMillis() + 60*10*1000) )
                .and()
                .signWith(generateKey())
                .compact();
    }

    public SecretKey generateKey()
    {
        byte[] decode = Decoders.BASE64.decode(getSecretKey());
    return Keys.hmacShaKeyFor(decode);
    }


//    public boolean validateToken(String token, Users user) {
//        try {
//            String username = Jwts.parserBuilder()
//                    .setSigningKey(generateKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//            return username.equals(user.getName());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//    public String extractUsername(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(generateKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }

}
