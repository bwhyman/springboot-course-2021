package org.example.jdbcexamples.repository;

import org.example.jdbcexamples.dox.User;
import org.example.jdbcexamples.dto.AddressUser;
import org.example.jdbcexamples.dto.UserAddress;
import org.example.jdbcexamples.dto.UserAddress3;
import org.example.jdbcexamples.mapper.UserAddress3ResultSetExtractor;
import org.example.jdbcexamples.mapper.UserAddressResultSetExtractor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    // 建议显式声明映射名称对应DTO中属性名称，避免冲突
    @Query("""
            select a.id as id, a.create_time as create_time, a.update_time as update_time, name, detail, a.user_id as user_id
            from user u join address a
            on u.id = a.user_id
            where a.id=:aid
            """)
    AddressUser findByAddressId(String aid);

    @Query("select * from user u limit :offet, :pageSize")
    List<User> findAll(long offset, int pageSize);

    @Query("select * from user u limit :#{#pageable.offset}, :#{#pageable.pageSize}")
    List<User> findAll(Pageable pageable);

    @Query("""
            select * from user u
            order by u.id desc
            limit :#{#pageable.offset}, :#{#pageable.pageSize}
            """)
    List<User> findByIdDesc(Pageable pageable);

    @Query(value = "select * from address a join user u on u.id = a.user_id where u.id=:uid",
            resultSetExtractorClass = UserAddress3ResultSetExtractor.class)
    UserAddress3 findUserAddress3(String uid);

    @Query(value = "select * from user u join address a on u.id = a.user_id where u.id=:uid",
            resultSetExtractorClass = UserAddressResultSetExtractor.class)
    UserAddress findUserAddress(String uid);
}
