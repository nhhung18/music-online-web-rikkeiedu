package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.StatusUserResp;
import com.ra.base_spring_boot.model.Banner;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.roles r WHERE u.id = :userId AND r.roleName = 'ROLE_ARTIST'")
    boolean existsByUserIdAndRoleArtist(@Param("userId") Long userId);

    // Lấy danh sách tất cả nghệ sĩ
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = 'ROLE_ARTIST' ORDER BY u.firstName ASC, u.lastName ASC")
    Page<User> findAllArtists(Pageable pageable);

    // Lấy danh sách nghệ sĩ nổi bật (dựa trên số lượng album và tổng lượt nghe)
    @Query("SELECT u FROM User u " +
            "JOIN u.roles r " +
            "LEFT JOIN u.albums a " +
            "LEFT JOIN a.songs s " +
            "LEFT JOIN s.songHistories sh " +
            "WHERE r.roleName = 'ROLE_ARTIST' " +
            "GROUP BY u.id " +
            "ORDER BY COALESCE(SUM(s.views), 0) DESC, COUNT(DISTINCT sh.id) DESC, COUNT(DISTINCT a.id) DESC")
    List<User> findFeaturedArtists(Pageable pageable);

    //  active status
    @Query("SELECT u " +
            "FROM User u WHERE u.status = 'ACTIVE'")
    Page<User> findAllByStatusActive(Pageable pageable);

    @Query("SELECT COUNT (u.id) FROM User u WHERE u.status = 'ACTIVE'")
    Long countActiveUser();

    //  block status
    @Query("SELECT u " +
            "FROM User u WHERE u.status = 'BLOCKED'")
    Page<User> findAllByStatusBlock(Pageable pageable);

    @Query("SELECT COUNT (u.id) FROM User u WHERE u.status = 'BLOCKED'")
    Long countBlockUser();

    //  verify status
    @Query("SELECT u " +
            "FROM User u WHERE u.status = 'VERIFY'")
    Page<User> findAllByStatusVerify(Pageable pageable);

    @Query("SELECT COUNT (u.id) FROM User u WHERE u.status = 'VERIFY'")
    Long countVerifyUser();

    boolean existsByEmail(String email);

    Page<User> findByFirstNameContainingIgnoreCase(Pageable pageable, String name);

    Page<User> findByLastNameContainingIgnoreCase(Pageable pageable, String name);

    //    @Query(value = "SELECT *\n" +
//            "FROM `user` u\n" +
//            "INNER JOIN user_role ur ON u.id = ur.user_id\n" +
//            "INNER JOIN `role` r ON ur.role_id = r.id\n" +
//            "WHERE r.role_name = 'ROLE_ARTIST';", nativeQuery = true)
    @Query(value = "SELECT u.* " +
            "FROM user u " +
            "INNER JOIN user_role ur ON u.id = ur.user_id " +
            "INNER JOIN role r ON ur.role_id = r.id " +
            "WHERE r.role_name = 'ROLE_ARTIST' AND (u.first_name LIKE %:name% " +
            "OR u.last_name LIKE %:name% " +
            "OR CONCAT(u.first_name, ' ', u.last_name) LIKE %:name%)",
            nativeQuery = true)
    Page<User> findByArtistName(Pageable pageable, String name);

    @Query(value = "SELECT u.* " +
            "FROM `user` u " +
            "INNER JOIN user_role ur ON u.id = ur.user_id " +
            "INNER JOIN `role` r ON ur.role_id = r.id " +
            "WHERE r.role_name = 'ROLE_ARTIST' AND u.last_name LIKE %:name%",
            nativeQuery = true)
    Page<User> findByLastArtistName(Pageable pageable, String name);

    User findArtistById(Long artistId);
}
