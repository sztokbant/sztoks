package br.net.du.myequity.controller.util;

import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.controller.viewmodel.transaction.TransactionViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import br.net.du.myequity.model.transaction.Transaction;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Method;
import lombok.NonNull;

public class ViewModelOutputUtils {

    public static final String VIEW_MODEL_OUTPUT_SUFFIX = "ViewModelOutput";

    public static Method getAccountSnapshotViewModelOutputFactoryMethod(
            @NonNull final Class<? extends AccountSnapshot> clazz)
            throws ClassNotFoundException, NoSuchMethodException {
        return getViewModelOutputClass(
                        clazz.getSimpleName().split("Snapshot")[0],
                        AccountSnapshotViewModelOutput.class)
                .getMethod("of", AccountSnapshot.class);
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
