package hu.davidder.webapp.core.base.category.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.webapp.core.base.category.entity.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

}