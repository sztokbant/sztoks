package br.net.du.sztoks.controller.util;

import br.net.du.sztoks.controller.viewmodel.account.AccountViewModelOutput;
import br.net.du.sztoks.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.sztoks.model.account.Account;
import br.net.du.sztoks.model.transaction.Transaction;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Method;
import lombok.NonNull;

public class ViewModelOutputUtils {

    public static final String VIEW_MODEL_OUTPUT_SUFFIX = "ViewModelOutput";

    public static Method getAccountViewModelOutputFactoryMethod(
            @NonNull final Class<? extends Account> clazz)
            throws ClassNotFoundException, NoSuchMethodException {
        return getViewModelOutputClass(clazz.getSimpleName(), AccountViewModelOutput.class)
                .getMethod("of", Account.class);
    }

    public static Method getTransactionViewModelOutputFactoryMethod(
            @NonNull final Class<? extends Transaction> clazz)
            throws ClassNotFoundException, NoSuchMethodException {
        return getViewModelOutputClass(clazz.getSimpleName(), TransactionViewModelOutput.class)
                .getMethod("of", Transaction.class);
    }

    @VisibleForTesting
    static Class<?> getViewModelOutputClass(final String prefix, final Class baseClass)
            throws ClassNotFoundException {
        return Class.forName(
                String.format(
                        "%s.%s%s",
                        baseClass.getPackage().getName(), prefix, VIEW_MODEL_OUTPUT_SUFFIX));
    }
}
