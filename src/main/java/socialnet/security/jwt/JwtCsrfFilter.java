package socialnet.security.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtCsrfFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository tokenRepository;
    private final HandlerExceptionResolver resolver;

    public JwtCsrfFilter(CsrfTokenRepository tokenRepository, HandlerExceptionResolver resolver) {
        this.tokenRepository = tokenRepository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        httpServletRequest.setAttribute(HttpServletResponse.class.getName(), httpServletResponse);
        CsrfToken csrfToken = this.tokenRepository.loadToken(httpServletRequest);
        boolean missingToken = csrfToken == null;
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(httpServletRequest);
            this.tokenRepository.saveToken(csrfToken, httpServletRequest, httpServletResponse);
        }

        httpServletRequest.setAttribute(CsrfToken.class.getName(), csrfToken);
        httpServletRequest.setAttribute(csrfToken.getParameterName(), csrfToken);
        if (httpServletRequest.getServletPath().equals("/auth/login")) {
            try {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (Exception e) {
                resolver.resolveException(httpServletRequest, httpServletResponse, null, new MissingCsrfTokenException(csrfToken.getToken()));
            }
        } else {
            String actualToken = httpServletRequest.getHeader(csrfToken.getHeaderName());
            if (actualToken == null) {
                actualToken = httpServletRequest.getParameter(csrfToken.getParameterName());
            }
            try {
                if (!StringUtils.isEmpty(actualToken)) {
                    Jwts.parser()
                            .setSigningKey(((JWTokenRepository) tokenRepository).getSecret())
                            .parseClaimsJws(actualToken);

                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } else
                    resolver.resolveException(httpServletRequest, httpServletResponse, null, new InvalidCsrfTokenException(csrfToken, actualToken));
            } catch (JwtException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Invalid CSRF token found for " + UrlUtils.buildFullRequestUrl(httpServletRequest));
                }

                if (missingToken) {
                    resolver.resolveException(httpServletRequest, httpServletResponse, null, new MissingCsrfTokenException(actualToken));
                } else {
                    resolver.resolveException(httpServletRequest, httpServletResponse, null, new InvalidCsrfTokenException(csrfToken, actualToken));
                }
            }
        }
    }
}
