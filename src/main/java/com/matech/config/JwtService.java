package com.matech.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @required: in order to start with this service three dependencies are required:
 * <p>
 *     <code>artifact</code> jjwt-api | jjwt-impl | jjwt-jackson
 * </p>
 * <p>
 *     <code>group </code> io.jsonwebtoken
 * </p>
 *
 * @description: this class will serve as jwt VALIDATION to our jwt filter in order
 * to save and extract values from our jwt token. jwt: json web token - it's a compact url saved
 *  <p>**Big 3 parts: header + payload + signature</p>
 *  <p>**header: type of token (jwt) and the algorithm (HS256)</p>
 *  <p>**payload: claim and data of the token (name, authories/roles, extra-claims)</p>
 *  <p>**verified-signature: secret used to verify that the sender is who he claimed to be and to ensure the message
 *  wasn't changed along the way along with a valid secret</p>
 *
 *
 */

@Service
public class JwtService {
    //https://www.vondy.com/random-key-generator--ZzGGMYgS?lc=5 + hex (256 bit) specs for the key
    private static final String SECRET_KEY = "e4f3b2a1c9d8e7f61324b5a6c7d8e9f0a1b2c3d4e5f60718293a4b5c6d7e8f9";

    // Last step - extract name
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); //the subject would be the email or username
    }

    // no extract claims to generate token
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Help to generate a token
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        // returning the new token out of the extract claims and user details
        return Jwts
                .builder()
                .setClaims(extraClaims) // set my claims
                .setSubject(userDetails.getUsername()) // set the subject (username or user email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // when this claim was created - help to check if the token is valid or not
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // how long the token should be valid - 24hrs + 1000 milliseconds
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // which key would sign this token and the signature algorithm
                .compact(); // it will generate and return the token
    }

    // Method to validate the token
    public boolean validateToken(String token, UserDetails userDetails) {
        // user details is used to check if the token belong to that particular userDetails

        final String username = extractUsername(token);

        //if username is equals to userDetails username and token is not expired
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // method checks if the date passed is before current date
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a single claim - Generic method passing a claims function as parameter
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Extract our JWT claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // starting building the parsed the token
                .setSigningKey(getSignInKey()) // generate a new signing key (our own method)
                .build() // build the object
                .parseClaimsJws(token) // then we can call the claims jws method
                .getBody(); // getting all the claims from the passed token
    }

    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
