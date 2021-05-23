package container.restaurant.server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsUserByNickname(String nickName);

    @Query(nativeQuery = true,
            value = "select u.* from tb_users as u\n" +
                    "join tb_feed as f\n" +
                    "on f.owner_id = u.id\n" +
                    "where DATE_FORMAT(f.created_date,'%Y-%m-%d') = CURDATE() \n" +
                    "order by f.created_date\n ")
    List<User> findByToDayFeedWriter();
}
