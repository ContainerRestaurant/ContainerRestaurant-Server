package container.restaurant.server.domain.contract.agree;

import container.restaurant.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractAgreeRepository extends JpaRepository<ContractAgree, Long> {
    ContractAgree findByUser(User user);
}
