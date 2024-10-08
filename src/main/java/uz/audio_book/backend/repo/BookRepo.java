package uz.audio_book.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uz.audio_book.backend.entity.Book;
import uz.audio_book.backend.model.projection.AdminBookProjection;
import uz.audio_book.backend.model.projection.BookProjection;
import uz.audio_book.backend.model.projection.SelectedBookProjection;

import java.util.List;
import java.util.UUID;

public interface BookRepo extends JpaRepository<Book, UUID> {

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT bc.categories_id) AS categoryIds,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
            GROUP BY b.id, b.title, b.author;
            """, nativeQuery = true)
    List<BookProjection> findAllProjections();

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT bc.categories_id) AS categoryIds,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
                     WHERE bc.categories_id IN (:categoriesIds)
            GROUP BY b.id, b.title, b.author
            LIMIT 6
            """, nativeQuery = true)
    List<BookProjection> findByPersonalCategories(List<UUID> categoriesIds);

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT bc.categories_id) AS categoryIds,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
            GROUP BY b.id, b.title, b.author, b.created_at
            ORDER BY b.created_at DESC
            LIMIT 6
            """, nativeQuery = true)
    List<BookProjection> findNewRelease();

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT bc.categories_id) AS categoryIds,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
                     LEFT JOIN comment c on b.id = c.book_id
            GROUP BY b.id, b.title, b.author
            ORDER BY count(c.*) DESC
            LIMIT 6
            """, nativeQuery = true)
    List<BookProjection> findTrendingNow();

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT bc.categories_id) AS categoryIds,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
                     LEFT JOIN comment c on b.id = c.book_id
            GROUP BY b.id, b.title, b.author
            ORDER BY RANDOM()
            LIMIT 6
            """, nativeQuery = true)
    List<BookProjection> findBestSeller();

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, ARRAY_AGG(c.name) AS categories,
                   b.created_at FROM book b
            JOIN public.book_categories bc ON b.id = bc.book_id
            JOIN public.category c ON c.id = bc.categories_id
            GROUP BY b.id, b.created_at
            ORDER BY b.created_at DESC
            """, nativeQuery = true)
    List<AdminBookProjection> findAllAdminBookProjection();

    @Query(value = """
            SELECT b.id,
                    b.title,
                    b.author,
                    b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                    ARRAY_AGG(DISTINCT ct.name) AS categoryNames,
                    ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                           FROM comment c
                           WHERE c.book_id = b.id), 2) AS rating,
                    b.description
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
                     LEFT JOIN comment c on b.id = c.book_id
                     JOIN category ct on bc.categories_id = ct.id
            WHERE b.title ILIKE :searchBy OR b.author ILIKE :searchBy
            GROUP BY b.id""", nativeQuery = true)
    List<SelectedBookProjection> findAllByAuthorOrTitle(String searchBy);

    @Query(value = """
            SELECT b.id,
                   b.title,
                   b.author,
                   b.photo_url,
                   b.audio_url,
                   b.pdf_url,
                   ARRAY_AGG(DISTINCT ct.name) AS categoryNames,
                   ROUND((SELECT SUM(c.rating) * 1.0 / COUNT(*)
                          FROM comment c
                          WHERE c.book_id = b.id), 2) AS rating,
                   b.description
            FROM book b
                     JOIN book_categories bc ON b.id = bc.book_id
                     LEFT JOIN comment c on b.id = c.book_id
                     JOIN category ct on bc.categories_id = ct.id
            WHERE b.id =:id
            GROUP BY b.id""", nativeQuery = true)
    SelectedBookProjection findSelectedBookByDetails(UUID id);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM book_categories
            WHERE categories_id = :categoryId
            """, nativeQuery = true)
    void deleteBookCategoryById(UUID categoryId);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM users_personal_categories
            WHERE personal_categories_id = :categoryId
            """, nativeQuery = true)
    void deletePersonalCategoriesById(UUID categoryId);
}