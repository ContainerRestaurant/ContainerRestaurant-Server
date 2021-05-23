package container.restaurant.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsUserByNickname(String nickName);

    @Query("select u from TB_USERS u join TB_FEED  f on f.owner.id = u.id where f.createdDate between ?1 and ?2")
    List<User> findByToDayFeedWriter(LocalDateTime to, LocalDateTime from);
}
