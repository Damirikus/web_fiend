package ru.gazizov.webfiend.repository;

import org.springframework.data.repository.CrudRepository;
import ru.gazizov.webfiend.model.Message;


/*в классе CrudRepository уже реализованы основные методы для работы с базой данных
так же можно создать дополнительные методы, название которых должны соответстоввать требованиЮ
после добавления сигнатуры метода, реализацию на себя берет спринг*/
public interface MessageRepository extends CrudRepository<Message, Long> {

}
