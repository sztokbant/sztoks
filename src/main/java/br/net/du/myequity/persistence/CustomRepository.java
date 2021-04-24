package br.net.du.myequity.persistence;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/** Ref.: https://stackoverflow.com/a/48878923/1259478 */
@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    void refresh(T t);
}
