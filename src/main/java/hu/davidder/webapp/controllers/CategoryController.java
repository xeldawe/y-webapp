package hu.davidder.webapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import hu.davidder.webapp.core.base.category.entity.Category;
import hu.davidder.webapp.core.base.category.repository.CategoryRepository;

@RestController
@RequestMapping("category")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CategoryController {

	@Lazy
	@Autowired
	private CategoryRepository categoryCrudRepository;
	
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category Category) {
        return ResponseEntity.ok(categoryCrudRepository.save(Category));
    }
    
    @PostMapping("bulk")
    public ResponseEntity<Iterable<Category>> createCategorys(@RequestBody List<Category> Categorys) {
        return ResponseEntity.ok(categoryCrudRepository.saveAll(Categorys));
    }
	
}
