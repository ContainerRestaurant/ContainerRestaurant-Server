package container.restaurant.server.domain.user.level;

import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserLevelFeedCountRepository extends JpaRepository<UserLevelFeedCount, Long> {

    Optional<UserLevelFeedCount> findByUserAndDate(User user, LocalDate date);

}
