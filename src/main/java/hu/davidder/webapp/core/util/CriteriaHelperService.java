package hu.davidder.webapp.core.util;

import java.util.Set;

import org.springframework.stereotype.Service;

import hu.davidder.webapp.core.base.EntityBase;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class CriteriaHelperService<T extends EntityBase> {

    public Set<Predicate> addAllBasePredicates(CriteriaBuilder cb, Root<T> root, Set<Predicate> predicates) {
        predicates.add(cb.isFalse(root.get("deleted")));
        predicates.add(cb.isTrue(root.get("status")));
        return predicates;
    }

    public Predicate[] getAsArray(Set<Predicate> set) {
        return set.toArray(new Predicate[0]);
    }
}