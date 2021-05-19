package container.restaurant.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsUserByNickname(String nickName);

    @Query("select u from TB_USERS u where u.feedCount <> 0 ORDER BY u.feedCount desc")
    List<User> findByFeedCountTopUser();
}
