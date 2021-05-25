package container.restaurant.server.web.linker;

import container.restaurant.server.web.ContractController;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ContractLinker {
    ContractController proxy
            = DummyInvocationUtils.methodOn(ContractController.class);

    public LinkBuilder getContract(){ return linkTo(proxy.getContract());}
}
