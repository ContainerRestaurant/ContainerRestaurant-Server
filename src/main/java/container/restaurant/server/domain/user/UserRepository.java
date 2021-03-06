package container.restaurant.server.domain.user;

import container.restaurant.server.web.dto.statistics.UserProfileDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdentifier(OAuth2Identifier identifier);

    boolean existsUserByNickname(String nickName);

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

    @Query("select distinct new container.restaurant.server.web.dto.statistics.UserProfileDto" +
                "(u.id, u.containerLevel, u.nickname, u.profile, max(f.createdDate)) " +
            "from TB_FEED f inner join f.owner u left outer join u.profile " +
            "group by u.id " +
            "order by max(f.createdDate) desc ")
    List<UserProfileDto> findLatestUsers(Pageable limit);

    @Query("select count(u.id) from TB_USERS u where u.feedCount > 0")
    long writerCount();
}
