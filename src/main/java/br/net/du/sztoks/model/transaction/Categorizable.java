package br.net.du.sztoks.model.transaction;

public interface Categorizable<T extends Enum> {
    T getCategory();

    void setCategory(T value);
}
