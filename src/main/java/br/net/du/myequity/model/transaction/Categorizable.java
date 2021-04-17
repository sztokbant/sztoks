package br.net.du.myequity.model.transaction;

public interface Categorizable<T> {
    T getCategory();

    void setCategory(T value);
}
