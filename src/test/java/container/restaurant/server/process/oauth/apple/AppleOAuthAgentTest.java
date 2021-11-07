package container.restaurant.server.process.oauth.apple;

import container.restaurant.server.config.auth.dto.OAuthAttributes;
import container.restaurant.server.domain.user.OAuth2Registration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// 애플 로그인 테스트... 애플 토큰은 만료기한이 10분이라 테스트는 무시한다.
@Disabled
class AppleOAuthAgentTest {

    @Test
    public void test() {
        AppleOAuthAgent appleOAuthAgent = new AppleOAuthAgent();

        String accessToken = "eyJraWQiOiJlWGF1bm1MIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiQ29udGFpbmVyLlJlc3RhdXJhbnQuaU9TIiwiZXhwIjoxNjM2Mzc3NTc2LCJpYXQiOjE2MzYyOTExNzYsInN1YiI6IjAwMDE3OC4zNWYzMWUyMGNkZGQ0ZDIyYmExNWE0YzBjOTFlZTg5MS4xNjI1IiwiY19oYXNoIjoiLXQ5R0dQdWtxWlBTUzRoVDY5c25mdyIsImVtYWlsIjoiYnY3cGQ3NTduakBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTYzNjI5MTE3Niwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.fmpwqeFnCPolC8W0drM-eBuCVpf212OLdgZ7hYCbrptzPjEgMBQWTPW9B41klmqeYODn1x2rFgedcAmfDlJrv300Lt2vsoeBg3ChnCbTxb_zlCSc-Kd8bSb9mzVMdr2XORHLtfpYFOD5nWbKIjK_FimdolzKZu4zkmlSo3Mmp_3LX9lnvzR6gKKNJCTDxyqkjWaCHE6ZfxViveDJ3vhicb7kCsUwZrctwL_u7H-eII7mJ_HkcTJf68zeRtQ890UEF4nFLwJs5-7J9va8VwLwkkowOi57GSSqj8k3h2hdJww57tvX5hA0qZ6J1T8VvgkVnjnEzz_JitNFU66nhe1Wtg";
        OAuthAttributes oAuthAttributes = appleOAuthAgent.getAuthAttrFrom(accessToken).orElse(null);

        assertThat(oAuthAttributes).isNotNull();
        assertThat(oAuthAttributes.getIdentifier())
                .matches(it -> it.getRegistration().equals(OAuth2Registration.APPLE));
    }

}