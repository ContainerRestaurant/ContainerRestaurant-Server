package container.restaurant.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsUserByNickname(String nickName);

    @Query("select distinct u from TB_USERS u join TB_FEED  f on f.owner.id = u.id where f.createdDate between ?1 and ?2")
    List<User> findByToDayFeedWriter(LocalDateTime to, LocalDateTime from);

    @Query(nativeQuery = true,
            value = "select  u.*, COUNT(f.id) as feedCountSum from tb_users as u \n" +
                    "join tb_feed as f \n" +
                    "on f.owner_id= u.id \n" +
                    "where f.created_date between ?1 and ?2\n" +
                    "group by u.id\n" +
                    "order by feedCountSum desc\n" +
                    "limit 10")
    List<User> findByFeedCountTopUsers(LocalDateTime to, LocalDateTime from);

    User findByPushTokenId(Long pushTokenId);
}
