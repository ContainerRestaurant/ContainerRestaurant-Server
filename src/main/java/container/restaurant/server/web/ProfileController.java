package container.restaurant.server.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        List<String> deployProfiles = Arrays.asList("deploy1", "deploy2");

        return profiles.stream()
                .filter(deployProfiles::contains)
                .findAny()
                .orElseGet(() -> profiles.isEmpty() ? "default" : profiles.get(0));
    }
}
