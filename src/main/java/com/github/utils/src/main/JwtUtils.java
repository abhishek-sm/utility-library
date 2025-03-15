package com.github.utils.src.main;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

/**
 * Utility class for generating, validating, and extracting information from JSON Web Tokens (JWTs).
 *
 * <p>
 * This class provides methods for creating JWT tokens with custom claims, validating tokens against a secret key,
 * extracting claims and subjects from tokens, and checking token expiration.
 */
@UtilityClass
public class JwtUtils {

    /**
     * Generates a JWT token with the given parameters.
     *
     * @param secretKey        The secret key for signing.
     * @param subject          The subject (usually user identifier).
     * @param claims           Additional claims to include.
     * @param expirationMillis Expiration time in milliseconds.
     * @return Signed JWT token as a String.
     */
    public static String generateToken(String secretKey, String subject, Map<String, Object> claims, long expirationMillis) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token with a custom signing algorithm.
     *
     * @param secretKey        The secret key.
     * @param subject          The subject (user ID).
     * @param claims           Additional claims.
     * @param expirationMillis Expiration time.
     * @param algorithm        The signature algorithm (e.g., HS256, HS512).
     * @return Signed JWT token.
     */
    public static String generateTokenWithAlgorithm(String secretKey, String subject, Map<String, Object> claims, long expirationMillis, SignatureAlgorithm algorithm) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, algorithm)
                .compact();
    }

    /**
     * Validates a JWT token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return True if valid, false otherwise.
     */
    public static boolean validateToken(String secretKey, String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts claims from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Claims object.
     */
    public static Claims extractClaims(String secretKey, String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extracts the subject from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Subject as String.
     */
    public static String extractSubject(String secretKey, String token) {
        return extractClaims(secretKey, token).getSubject();
    }

    /**
     * Checks if the token is expired.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return True if expired, false otherwise.
     */
    public static boolean isTokenExpired(String secretKey, String token) {
        return extractClaims(secretKey, token).getExpiration().before(new Date());
    }

    /**
     * Extracts a specific claim from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @param claimKey  The key of the claim to extract.
     * @return The claim value as an Object (can be cast to String, Integer, etc.).
     */
    public static Object extractClaim(String secretKey, String token, String claimKey) {
        return extractClaims(secretKey, token).get(claimKey);
    }

    /**
     * Refreshes a JWT token with a new expiration time.
     *
     * @param secretKey        The secret key.
     * @param token            The existing token.
     * @param newExpirationMs  New expiration time in milliseconds.
     * @return The refreshed token.
     */
    public static String refreshToken(String secretKey, String token, long newExpirationMs) {
        Claims claims = extractClaims(secretKey, token);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + newExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if a JWT token will expire soon.
     *
     * @param secretKey    The secret key.
     * @param token        The JWT token.
     * @param bufferTimeMs Time before expiry to consider (e.g., 5 mins).
     * @return True if the token will expire soon, otherwise false.
     */
    public static boolean isTokenExpiringSoon(String secretKey, String token, long bufferTimeMs) {
        Date expiration = extractClaims(secretKey, token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis() < bufferTimeMs;
    }

    /**
     * Extracts the expiration date of the JWT token.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return Expiration date.
     */
    public static Date getExpirationDate(String secretKey, String token) {
        return extractClaims(secretKey, token).getExpiration();
    }

    /**
     * Generates a random secure 256-bit secret key.
     *
     * @return A Base64 encoded secret key.
     */
    public static String generateRandomSecretKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Decodes a JWT token without validation.
     *
     * @param token The JWT token.
     * @return Claims (decoded payload).
     */
    public static Claims decodeTokenWithoutValidation(String token) {
        return Jwts.parserBuilder().build().parseClaimsJws(token).getBody();
    }

    /**
     * Gets the remaining lifetime of the token in milliseconds.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return Remaining lifetime in milliseconds.
     */
    public static long getTokenRemainingTime(String secretKey, String token) {
        Date expiration = extractClaims(secretKey, token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Extracts the JWT token from an Authorization header.
     *
     * @param authHeader The Authorization header (e.g., "Bearer <token>").
     * @return The extracted JWT token or null if invalid.
     */
    public static String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Validates a JWT token and returns error messages if invalid.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return "Valid" if token is valid, otherwise an error message.
     */
    public static String validateTokenWithError(String secretKey, String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return "Valid";
        } catch (ExpiredJwtException e) {
            return "Token is expired!";
        } catch (UnsupportedJwtException e) {
            return "Unsupported JWT token!";
        } catch (MalformedJwtException e) {
            return "Malformed JWT token!";
        } catch (IllegalArgumentException e) {
            return "Token is empty or null!";
        }
    }

}
