package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Находит количество пользователей с указанной электронной почтой
     * @param eMail адрес электронной почты
     * @return количество пользователей
     */
    @Query("select count(u) from users u where email = :eMail")
    byte countByEmail(String eMail);
}
