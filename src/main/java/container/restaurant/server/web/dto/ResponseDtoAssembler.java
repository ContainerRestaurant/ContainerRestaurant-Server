package container.restaurant.server.web.dto;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Entity 를 응답 DTO 로 변환하는 Assembler 를 구현하기 위한 인터페이스
 * @param <T> Entity 타입
 * @param <D> DTO 타입
 */
public interface ResponseDtoAssembler<T, D extends RepresentationModel<D>>
        extends RepresentationModelAssembler<T, D>
{
    /**
     * {@link javax.persistence.Entity} 를 DTO 로 변환하는 함수를 제공해야한다.
     * @return Entity 를 DTO 로 변환하는 {@link Function}
     */
    Function<T, D> converter();

    /**
     * 싱글 DTO 에 추가해줄 링크를 제공해야한다.<p/>
     * 최소 self 링크를 생성하고, 관련된 동작들에 대한 링크를 정의하기를 권장한다.
     * @param entity 링크를 생성하는데 참조할 Entity
     * @return Entity 를 참조해서 만든 링크 목록
     */
    Iterable<Link> links(T entity);

    /**
     * 엔티티에 대해 추가 권한이 있는 경우 싱글 DTO 에 추가해줄 링크를 구현한다.
     * @param entity 추가 링크를 생성하는데 참조할 Entity
     * @return Entity 를 참조해서 만든 추가 링크 목록
     */
    default Iterable<Link> authLinks(T entity) {
        return List.of();
    }

    /**
     * 1 개 이상의 리소스를 포함한 DTO 에 추가해줄 링크를 구현한다.<p/>
     * self 링크와 관련된 동작들에 대한 링크를 정의하기를 권장한다.
     * @param entities 링크를 생성하는데 참조할 Entity 목록
     * @return collection 을 참조해서 만든 링크 목록
     */
    default Iterable<Link> links(Iterable<? extends T> entities) {
        return List.of();
    }

    /**
     * 추가 권한이 있는 1 개 이상의 리소스를 포함한 DTO 에 추가해줄 링크를 구현한다.<p/>
     * @param entities 링크를 생성하는데 참조할 Entity 목록
     * @return collection 을 참조해서 만든 링크 목록
     */
    default Iterable<Link> authLinks(Iterable<? extends T> entities) {
        return List.of();
    }

    default D toModel(T entity) {

        return toModel(entity, false);
    }

    default D toModel(T entity, Boolean auth) {

        return converter().apply(entity)
                .add(links(entity))
                .addAllIf(auth, () -> authLinks(entity));
    }

    default CollectionModel<D> toCollectionModel(Iterable<? extends T> entities) {

        return toCollectionModel(entities, false);
    }

    default CollectionModel<D> toCollectionModel(Iterable<? extends T> entities, Boolean auth) {

        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> this.toModel(t, true)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of))
                .add(links(entities))
                .addAllIf(auth, () -> authLinks(entities));
    }
}
