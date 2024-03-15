package guru.qa.niffler.service;

import guru.qa.grpc.niffler.grpc.*;
import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.model.CategoryJson;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@GrpcService
public class GrpcCategoryService extends NifflerCategoryServiceGrpc.NifflerCategoryServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    private static final int MAX_CATEGORIES_SIZE = 7;
    private final CategoryRepository categoryRepository;

    @Autowired
    public GrpcCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void getCategories(UserName request, StreamObserver<Categories> responseObserver) {
        List<CategoryJson> categories = categoryRepository.findAllByUsername(request.getName())
                .stream()
                .map(CategoryJson::fromEntity)
                .toList();

        Categories response = Categories.newBuilder().addAllCategories(
                        categories.stream()
                                .map(category ->
                                        Category.newBuilder()
                                                .setId(category.id().toString())
                                                .setCategory(category.category())
                                                .setUsername(category.username())
                                                .build()
                                ).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addCategory(CategoryRequest request, StreamObserver<Category> responseObserver) {
        CategoryJson category;
        final String username = request.getUsername();
        final String categoryName = request.getCategory();

        if (categoryRepository.findAllByUsername(username).size() > MAX_CATEGORIES_SIZE) {
            LOG.error("### Can`t add over than 7 categories for user: " + username);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "Can`t add over than 7 categories for user: '" + username);
        }

        CategoryEntity ce = new CategoryEntity();
        ce.setCategory(categoryName);
        ce.setUsername(username);
        try {
            category = CategoryJson.fromEntity(categoryRepository.save(ce));
        } catch (DataIntegrityViolationException e) {
            LOG.error("### Error while creating category: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Category with name '" + categoryName + "' already exists", e);
        }

        Category response = Category.newBuilder()
                .setId(category.id().toString())
                .setCategory(category.category())
                .setUsername(category.username())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
