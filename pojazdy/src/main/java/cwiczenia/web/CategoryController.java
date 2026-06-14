package cwiczenia.web;

import cwiczenia.models.VehicleCategoryConfig;
import cwiczenia.services.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final VehicleCategoryConfigService categoryService;

    public CategoryController(VehicleCategoryConfigService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return categoryService.getByCategory(category);
    }
}
