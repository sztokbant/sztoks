package br.net.du.myequity.controller.util;

import br.net.du.myequity.controller.viewmodel.accountsnapshot.AccountSnapshotViewModelOutput;
import br.net.du.myequity.model.snapshot.AccountSnapshot;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Method;
import lombok.NonNull;

public class ViewModelOutputUtils {

    public static Method getViewModelOutputFactoryMethod(
            @NonNull final Class<? extends AccountSnapshot> clazz)
            throws ClassNotFoundException, NoSuchMethodException {
        return getViewModelOutputClass(clazz).getMethod("of", AccountSnapshot.class);
    }

    @VisibleForTesting
    static Class getViewModelOutputClass(@NonNull final Class<? extends AccountSnapshot> clazz)
            throws ClassNotFoundException {
        final String className = clazz.getSimpleName();
        final String prefix = className.split("Snapshot")[0];

        return Class.forName(
                String.format(
                        "%s.%s%s",
                        AccountSnapshotViewModelOutput.class.getPackage().getName(),
                        prefix,
                        "ViewModelOutput"));
    }
}
