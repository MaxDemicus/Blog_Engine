package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Находит пользователя с указанной электронной почтой
     * @param eMail адрес электронной почты
     * @return пользователь
     */
    Optional<User> findByEmail(String eMail);
}
