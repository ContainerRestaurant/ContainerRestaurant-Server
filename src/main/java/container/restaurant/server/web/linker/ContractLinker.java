package container.restaurant.server.web.linker;

import container.restaurant.server.config.auth.dto.SessionUser;
import container.restaurant.server.web.ContractController;
import container.restaurant.server.web.dto.contract.ContractAgreeDto;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ContractLinker {
    ContractController proxy
            = DummyInvocationUtils.methodOn(ContractController.class);

    SessionUser u =
            DummyInvocationUtils.methodOn(SessionUser.class);

    ContractAgreeDto dto =
            DummyInvocationUtils.methodOn(ContractAgreeDto.class);

    public LinkBuilder getContract(){ return linkTo(proxy.getContract());}

    public LinkBuilder agreeContract(){ return linkTo(proxy.agreeContract(u, dto)); }
}
