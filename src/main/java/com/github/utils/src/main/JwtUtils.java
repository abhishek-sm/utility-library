package com.github.utils.src.main;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for generating, validating, and extracting information from JSON Web Tokens (JWTs).
 *
 * <p>
 * This class provides methods for creating JWT tokens with custom claims, validating tokens against a secret key,
 * extracting claims and subjects from tokens, and checking token expiration.
 * It supports both symmetric and asymmetric signing algorithms.
 * </p>
 */
@Slf4j
@Data
@UtilityClass
public final class JwtUtils {

    private static final Duration DEFAULT_TOKEN_LIFETIME = Duration.ofHours(1);
    private static final Duration DEFAULT_REFRESH_TOKEN_LIFETIME = Duration.ofDays(7);
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Generates a JWT token with the given parameters.
     *
     * @param secretKey        The secret key for signing.
     * @param subject          The subject (usually user identifier).
     * @param claims           Additional claims to include.
     * @param expirationMillis Expiration time in milliseconds.
     * @return Signed JWT token as a String.
     * @throws IllegalArgumentException if secretKey or subject is null or empty
     */
    public static String generateToken(String secretKey, String subject, Map<String, Object> claims, long expirationMillis) {
        validateInputs(secretKey, subject);

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256);

        if (claims != null && !claims.isEmpty()) {
            builder.setClaims(claims);
            // Re-set the subject as setClaims overwrites all claims including subject
            builder.setSubject(subject);
        }

        return builder.compact();
    }

    /**
     * Generates a standard access token with default expiration (1 hour).
     *
     * @param secretKey The secret key for signing.
     * @param subject   The subject (usually user identifier).
     * @param claims    Additional claims to include.
     * @return Signed JWT token as a String.
     */
    public static String generateAccessToken(String secretKey, String subject, Map<String, Object> claims) {
        return generateToken(secretKey, subject, claims, DEFAULT_TOKEN_LIFETIME.toMillis());
    }

    /**
     * Generates a refresh token with extended expiration (7 days).
     *
     * @param secretKey The secret key for signing.
     * @param subject   The subject (usually user identifier).
     * @return Signed JWT refresh token as a String.
     */
    public static String generateRefreshToken(String secretKey, String subject) {
        return generateToken(secretKey, subject, Map.of("token_type", "refresh"), DEFAULT_REFRESH_TOKEN_LIFETIME.toMillis());
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
     * @throws IllegalArgumentException if secretKey or subject is null or empty
     */
    public static String generateTokenWithAlgorithm(String secretKey, String subject, Map<String, Object> claims,
                                                    long expirationMillis, SignatureAlgorithm algorithm) {
        validateInputs(secretKey, subject);
        if (algorithm == null) {
            throw new IllegalArgumentException("Signature algorithm cannot be null");
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, algorithm);

        if (claims != null && !claims.isEmpty()) {
            builder.setClaims(claims);
            // Re-set the subject as setClaims overwrites all claims including subject
            builder.setSubject(subject);
        }

        return builder.compact();
    }

    /**
     * Generates a JWT token using asymmetric key pair (RS256, RS384, RS512).
     *
     * @param privateKey       The private key for signing.
     * @param subject          The subject (user ID).
     * @param claims           Additional claims.
     * @param expirationMillis Expiration time.
     * @param algorithm        The signature algorithm (e.g., RS256).
     * @return Signed JWT token.
     * @throws IllegalArgumentException if privateKey or subject is null
     */
    public static String generateTokenWithAsymmetricKey(Key privateKey, String subject, Map<String, Object> claims,
                                                        long expirationMillis, SignatureAlgorithm algorithm) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Signature algorithm cannot be null");
        }

        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(privateKey, algorithm);

        if (claims != null && !claims.isEmpty()) {
            builder.setClaims(claims);
            // Re-set the subject as setClaims overwrites all claims including subject
            builder.setSubject(subject);
        }

        return builder.compact();
    }

    /**
     * Validates a JWT token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return True if valid, false otherwise.
     * @throws IllegalArgumentException if secretKey or token is null or empty
     */
    public static boolean validateToken(String secretKey, String token) {
        validateInputs(secretKey, token);

        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a JWT token signed with an asymmetric key.
     *
     * @param publicKey The public key used for validation.
     * @param token     The JWT token.
     * @return True if valid, false otherwise.
     */
    public static boolean validateTokenWithAsymmetricKey(Key publicKey, String token) {
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key cannot be null");
        }
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed with asymmetric key: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts claims from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Claims object.
     * @throws JwtException if token is invalid
     */
    public static Claims extractClaims(String secretKey, String token) {
        validateInputs(secretKey, token);

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts claims from the token without throwing exceptions.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Optional containing Claims if token is valid, empty Optional otherwise.
     */
    public static Optional<Claims> extractClaimsSafely(String secretKey, String token) {
        try {
            return Optional.of(extractClaims(secretKey, token));
        } catch (Exception e) {
            log.warn("Failed to extract claims from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Generic method to extract a specific claim from token.
     *
     * @param <T>         The type of the claim to extract.
     * @param secretKey   The secret key used for validation.
     * @param token       The JWT token.
     * @param claimsResolver Function that resolves the desired claim from Claims object.
     * @return The claim value.
     */
    public static <T> T extractClaim(String secretKey, String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(secretKey, token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the subject from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Subject as String.
     * @throws JwtException if token is invalid
     */
    public static String extractSubject(String secretKey, String token) {
        validateInputs(secretKey, token);
        return extractClaim(secretKey, token, Claims::getSubject);
    }

    /**
     * Extracts the subject from the token without throwing exceptions.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return Optional containing subject if token is valid, empty Optional otherwise.
     */
    public static Optional<String> extractSubjectSafely(String secretKey, String token) {
        try {
            return Optional.of(extractSubject(secretKey, token));
        } catch (Exception e) {
            log.warn("Failed to extract subject from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if the token is expired.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @return True if expired, false otherwise.
     * @throws JwtException if token is invalid
     */
    public static boolean isTokenExpired(String secretKey, String token) {
        validateInputs(secretKey, token);

        try {
            Date expiration = extractClaim(secretKey, token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts a specific claim from the token.
     *
     * @param secretKey The secret key used for validation.
     * @param token     The JWT token.
     * @param claimKey  The key of the claim to extract.
     * @return The claim value as an Object (can be cast to String, Integer, etc.).
     * @throws JwtException if token is invalid
     */
    public static Object extractClaim(String secretKey, String token, String claimKey) {
        validateInputs(secretKey, token);

        if (claimKey == null || claimKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Claim key cannot be null or empty");
        }

        return extractClaims(secretKey, token).get(claimKey);
    }

    /**
     * Typed version of extractClaim.
     *
     * @param <T>        The type to cast the claim to.
     * @param secretKey  The secret key used for validation.
     * @param token      The JWT token.
     * @param claimKey   The key of the claim to extract.
     * @param type       The Class object for T.
     * @return The claim value cast to type T.
     * @throws ClassCastException if the claim cannot be cast to T
     */
    public static <T> T extractClaim(String secretKey, String token, String claimKey, Class<T> type) {
        Object claim = extractClaim(secretKey, token, claimKey);
        return type.cast(claim);
    }

    /**
     * Safely extract a typed claim with fallback to default value.
     *
     * @param <T>          The type to cast the claim to.
     * @param secretKey    The secret key used for validation.
     * @param token        The JWT token.
     * @param claimKey     The key of the claim to extract.
     * @param type         The Class object for T.
     * @param defaultValue The default value to return if claim extraction fails.
     * @return The claim value cast to type T, or defaultValue if extraction fails.
     */
    public static <T> T extractClaimOrDefault(String secretKey, String token, String claimKey,
                                              Class<T> type, T defaultValue) {
        try {
            Object claim = extractClaim(secretKey, token, claimKey);
            if (claim == null) {
                return defaultValue;
            }
            return type.cast(claim);
        } catch (Exception e) {
            log.debug("Failed to extract claim {}: {}", claimKey, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Refreshes a JWT token with a new expiration time.
     *
     * @param secretKey        The secret key.
     * @param token            The existing token.
     * @param newExpirationMs  New expiration time in milliseconds.
     * @return The refreshed token.
     * @throws JwtException if token is invalid
     */
    public static String refreshToken(String secretKey, String token, long newExpirationMs) {
        validateInputs(secretKey, token);

        try {
            Claims claims = extractClaims(secretKey, token);
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Instant now = Instant.now();

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusMillis(newExpirationMs)))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (ExpiredJwtException e) {
            // Special handling for expired tokens - allow refresh if within grace period
            log.debug("Refreshing an expired token");
            Claims claims = e.getClaims();
            Date expiration = claims.getExpiration();

            // Allow refresh if expired less than 24 hours ago
            if (System.currentTimeMillis() - expiration.getTime() <= Duration.ofHours(24).toMillis()) {
                SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
                Instant now = Instant.now();

                return Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(Date.from(now))
                        .setExpiration(Date.from(now.plusMillis(newExpirationMs)))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
            } else {
                throw new JwtException("Token expired beyond refresh grace period");
            }
        }
    }

    /**
     * Checks if a JWT token will expire soon.
     *
     * @param secretKey    The secret key.
     * @param token        The JWT token.
     * @param bufferTimeMs Time before expiry to consider (e.g., 5 mins).
     * @return True if the token will expire soon, otherwise false.
     * @throws JwtException if token is invalid
     */
    public static boolean isTokenExpiringSoon(String secretKey, String token, long bufferTimeMs) {
        validateInputs(secretKey, token);

        try {
            Date expiration = extractClaim(secretKey, token, Claims::getExpiration);
            return expiration.getTime() - System.currentTimeMillis() < bufferTimeMs;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts the expiration date of the JWT token.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return Expiration date.
     * @throws JwtException if token is invalid
     */
    public static Date getExpirationDate(String secretKey, String token) {
        validateInputs(secretKey, token);
        return extractClaim(secretKey, token, Claims::getExpiration);
    }

    /**
     * Generates a random secure 256-bit secret key.
     *
     * @return A Base64 encoded secret key.
     */
    public static String generateRandomSecretKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Generates a random key pair for asymmetric signing (RSA).
     *
     * @return A KeyPair containing a private and public key.
     */
    public static KeyPair generateRsaKeyPair() {
        return Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    /**
     * Converts Base64 encoded public key to Key object.
     *
     * @param base64PublicKey Base64 encoded public key.
     * @return Key object.
     */
    public static Key parsePublicKey(String base64PublicKey) {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Decodes a JWT token without validation.
     * WARNING: This method doesn't verify the signature, so it should only be used
     * when the token integrity is guaranteed by other means.
     *
     * @param token The JWT token.
     * @return Claims (decoded payload).
     * @throws JwtException if the token format is invalid
     */
    public static Claims decodeTokenWithoutValidation(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            return Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(60) // Allow for some clock drift
                    .build()
                    .parseClaimsJwt(extractUnsignedToken(token))
                    .getBody();
        } catch (JwtException e) {
            throw new JwtException("Failed to decode token: " + e.getMessage(), e);
        }
    }

    /**
     * Extract the unsigned JWT token (header and claims only) from a signed token.
     *
     * @param token The signed JWT token.
     * @return The unsigned JWT (header.payload).
     */
    private static String extractUnsignedToken(String token) {
        int lastDotIndex = token.lastIndexOf('.');
        if (lastDotIndex < 0) {
            throw new JwtException("Invalid JWT token format");
        }
        return token.substring(0, lastDotIndex + 1);
    }

    /**
     * Gets the remaining lifetime of the token in milliseconds.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return Remaining lifetime in milliseconds.
     * @throws JwtException if token is invalid
     */
    public static long getTokenRemainingTime(String secretKey, String token) {
        validateInputs(secretKey, token);

        try {
            Date expiration = extractClaim(secretKey, token, Claims::getExpiration);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingTime); // Never return negative time
        } catch (ExpiredJwtException e) {
            return 0; // Token is already expired
        }
    }

    /**
     * Extracts the JWT token from an Authorization header.
     *
     * @param authHeader The Authorization header (e.g., "Bearer <token>").
     * @return Optional containing the extracted JWT token, or empty if invalid.
     */
    public static Optional<String> extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
            if (!token.isEmpty()) {
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

    /**
     * Validates a JWT token and returns error messages if invalid.
     *
     * @param secretKey The secret key.
     * @param token     The JWT token.
     * @return TokenValidationResult containing validation status and any error message.
     */
    public static TokenValidationResult validateTokenWithDetails(String secretKey, String token) {
        validateInputs(secretKey, token);

        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return new TokenValidationResult(true, "Valid");
        } catch (ExpiredJwtException e) {
            return new TokenValidationResult(false, "Token is expired");
        } catch (UnsupportedJwtException e) {
            return new TokenValidationResult(false, "Unsupported JWT token format");
        } catch (MalformedJwtException e) {
            return new TokenValidationResult(false, "Malformed JWT token");
        } catch (SignatureException e) {
            return new TokenValidationResult(false, "Invalid JWT signature");
        } catch (JwtException e) {
            return new TokenValidationResult(false, "JWT token validation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return new TokenValidationResult(false, "JWT token is empty or null");
        }
    }

    /**
     * Creates an authorization header value with the token.
     *
     * @param token The JWT token.
     * @return Authorization header value (e.g., "Bearer <token>").
     */
    public static String createAuthorizationHeader(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return TOKEN_PREFIX + token;
    }

    /**
     * Parses a Base64 encoded HMAC secret key.
     *
     * @param base64SecretKey Base64 encoded secret key.
     * @return SecretKey object.
     */
    public static SecretKey parseSecretKey(String base64SecretKey) {
        if (base64SecretKey == null || base64SecretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }

        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validates inputs for common methods.
     *
     * @param secretKey The secret key.
     * @param tokenOrSubject The token or subject.
     * @throws IllegalArgumentException if any input is invalid
     */
    private static void validateInputs(String secretKey, String tokenOrSubject) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        if (tokenOrSubject == null || tokenOrSubject.trim().isEmpty()) {
            throw new IllegalArgumentException("Token/Subject cannot be null or empty");
        }
    }

    /**
     * Class representing the result of token validation.
     */
    public static class TokenValidationResult {
        private final boolean valid;
        private final String message;

        public TokenValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        @Override
        public String toString() {
            return "TokenValidationResult{valid=" + valid + ", message='" + message + "'}";
        }
    }
    
}
