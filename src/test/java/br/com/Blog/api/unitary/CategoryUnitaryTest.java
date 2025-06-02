package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import br.com.Blog.api.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryUnitaryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testThrowBadRequestInGet() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.categoryService.get(-1L);
        });

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            this.categoryService.get(0L);
        });

        assertNotNull(exception);
        assertNotNull(exception1);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
    }

    @Test
    public void testGetCategory() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Category categoryFound = this.categoryService.get(category.getId());

        assertNotNull(categoryFound);

        assertEquals(categoryFound.getName(), category.getName());
        assertEquals(categoryFound.getName(), category.getName());
        assertEquals(categoryFound.getId(), category.getId());

        verify(categoryRepository, times(1)).findById(category.getId());
    }

    @Test
    public void testThrowNotFoundInGet() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
           this.categoryService.get(999L);
        });

        assertNotNull(exception);

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testListCategory() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("C1");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("C2");

        Category cat3 = new Category();
        cat3.setId(3L);
        cat3.setName("C3");

        List<Category> categoryList = List.of(
          cat1, cat2, cat3
        );

        when(categoryRepository.findAll()).thenReturn(categoryList);

        var response = categoryService.getAll();

        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals(cat1.getName(), response.getFirst().getName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteCategory() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        doNothing().when(this.categoryRepository).delete(category);

        this.categoryService.delete(category);

        verify(categoryRepository, times(1)).delete(category);

    }

    @Test
    public void testCreateCategory() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();
        category.setName("TI");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("TI");
        savedCategory.setUser(user);

        when(categoryRepository.existsByName("TI")).thenReturn(false);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category categoryCreated = this.categoryService.create(category, user);

        assertNotNull(categoryCreated);
        assertEquals("TI", categoryCreated.getName());
        assertEquals(user, categoryCreated.getUser());
        assertEquals(1L, categoryCreated.getId());

        verify(categoryRepository, times(1)).existsByName("TI");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();
        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Category categoryBeforeUpdated = new Category();
        categoryBeforeUpdated.setName("TIS");

        Category categoryAfterUpdated = new Category();
        categoryAfterUpdated.setId(1L);
        categoryAfterUpdated.setName("TIS");
        categoryAfterUpdated.setUser(user);

        when(categoryRepository.save(any(Category.class))).thenReturn(categoryAfterUpdated);

        Category categoryUpdated = this.categoryService.update(category, categoryBeforeUpdated);

        assertNotNull(categoryUpdated);
        assertEquals("TIS", categoryUpdated.getName());
        assertEquals(user, categoryUpdated.getUser());
        assertEquals(1L, categoryUpdated.getId());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

}
