package hu.davidder.webapp.core.base.order.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hu.davidder.webapp.core.base.order.entity.Order;
import hu.davidder.webapp.core.util.CriteriaHelperService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CriteriaHelperService<Order> criteriaHelperService;

    @Override
    public List<Order> customFindAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);
        Set<Predicate> predicates = new HashSet<>();
        criteriaHelperService.addAllBasePredicates(cb, root, predicates);
        query.where(criteriaHelperService.getAsArray(predicates));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<Order> customFindById(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        Set<Predicate> predicates = new HashSet<>();
        predicates.add(cb.equal(root.get("id"), id));
        criteriaHelperService.addAllBasePredicates(cb, root, predicates);
        query.where(criteriaHelperService.getAsArray(predicates));

        List<Order> results = entityManager.createQuery(query).getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
