package hello.mapper;

import hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("select * from user where id = #{id}")
    User findUserById(@Param("id") Integer id);

    @Update("insert into user(username, encrypted_password, created_at,updated_at)" +
            "values(#{username}, #{encryptedPassword}, now(), now())")
    void save(@Param("username") String username,
              @Param("encryptedPassword") String encryptedPassword);
}
