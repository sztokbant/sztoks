package br.net.du.myequity.model.transaction;

public interface Categorizable<T extends Enum> {
    T getCategory();

    void setCategory(T value);
}
